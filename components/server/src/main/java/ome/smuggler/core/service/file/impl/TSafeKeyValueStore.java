package ome.smuggler.core.service.file.impl;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Stream;

import ome.smuggler.core.service.file.KeyValueStore;
import util.object.Identifiable;
import util.types.PositiveN;


/**
 * Wraps a {@link KeyValueStore} to make it thread safe.
 * This class uses lock striping to serialise operations on the same key while
 * at the same time allowing operations on different keys to run in parallel.
 */
public class TSafeKeyValueStore<K extends Identifiable, V>
        implements KeyValueStore<K, V> {

    private final KeyValueStore<K, V> target;
    private final Lock[] lockStripes;  // lock striping, see note at the bottom.

    /**
     * Creates a new instance to add thread-safety to the underlying
     * {@link KeyValueStore}.
     * @param target the underlying object providing the functionality.
     * @param numberOfStripes how many locks to use for striping.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public TSafeKeyValueStore(KeyValueStore<K, V> target,
                              PositiveN numberOfStripes) {
        requireNonNull(target, "target");
        requireNonNull(numberOfStripes, "numberOfStripes");

        this.target = target;
        this.lockStripes = Stream.generate(ReentrantLock::new)
                                 .limit(numberOfStripes.get())
                                 .toArray(Lock[]::new);
    }

    protected Lock lookupLock(K key) {
        int index = key.hashedId(lockStripes.length);
        return lockStripes[index];
    }

    protected void withLock(K key, Runnable action) {
        requireNonNull(key, "key");

        Lock lock = lookupLock(key);
        lock.lock();
        try {
            action.run();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void put(K key, V value) {
        withLock(key, () -> target.put(key, value));
    }

    @Override
    public void modify(K key, Function<V, V> operation) {
        withLock(key, () -> target.modify(key, operation));
    }

    @Override
    public void remove(K key) {
        withLock(key, () -> target.remove(key));
    }

}
/* NOTE. Lock striping.
 * It'd be simpler to use one lock for all operations---put, modify, and remove.
 * But that would also be a bottleneck! Because each key corresponds to a
 * separate file, we could in principle use a separate lock for each key, so
 * for example modify(k1, ...) and modify(k1, ...) would use the same lock l1
 * and be serialised, but modify(k2, ...) would use a different lock l2 and
 * run in parallel w/r/t e.g. modify(k1, ...).
 *
 * Now, a naive implementation using a map { key -> lock } won't do as we'd
 * end up with one gazillion locks in the map overtime and removing locks from
 * the map while maintaining thread-safety is not trivial. Think of this as an
 * example:
 *   1. Thread t1 wants to perform an operation for key k. There's no lock in
 *      the map m for k so we create one m[k] = l1.
 *   2. Thread t2 comes along to perform another operation for the same key k,
 *      so it gets a reference to l1.
 *   3. t1 acquires l1 and t2 waits on t1 to release l1.
 *   4. t1 releases l1 and removes it from m.
 *   5. t2 acquires l1 and starts the operation, but is suspended before the
 *      operation completes.
 *   6. Another thread t3 kicks in to do an operation still with key k, but
 *      there's no lock in m for k, so we create one m[k] = l2 and this is
 *      what t2 gets.
 *   7. t2 and t3 now have different locks so their operations can run in
 *      parallel (on a multi-core box) but we wanted to serialise operations
 *      for the same key k!
 *
 * What about removing the lock from the map only when there are no threads
 * blocked on it? According to the JDK API docs, there's no reliable way to
 * tell how many threads are waiting on a lock, so we'd have to roll out our
 * own counting mechanism, and that should also take reentrancy into account,
 * and it should also...ya, the list goes on.
 *
 * Lock striping is instead easy enough to roll out and use. The invariant we
 * enforce is simply that
 *
 *    k1, k2 keys, L(k) lock associated to key k
 *
 *    k1 = k2   ==>  L(k1) = L(k2)
 *
 * so that operations for the same key will get the same lock and will be
 * serialised. But we can't ensure different keys will always get a separate
 * lock---i.e. potentially less parallelism, but that's unlikely for a good
 * choice of hashing and number of stripes. On the other hand we have a bounded
 * number of locks and no need to synchronise the retrieval of a lock---which
 * we'd have to do in the above scenario, i.e. less parallelism.
 */
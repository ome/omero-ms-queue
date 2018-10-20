package kew.providers.artemis.qchan;

import static java.util.Objects.requireNonNull;

import org.apache.activemq.artemis.api.core.client.ClientSession;

import util.error.Exceptions;
import util.lambda.ActionE;
import util.lambda.SupplierE;

/**
 * Enforces serial access to a given Artemis session instance.
 *
 * Artemis client sessions aren't thread-safe.
 * If two threads try to send or ack a message concurrently, you might get this
 * warning in the logs
 * <pre>
 *     AMQ212051: Invalid concurrent session usage. Sessions are not
 *     supposed to be used by more than one thread concurrently.
 * </pre>
 * (Have a look at the {@code startCall} method of {@code ClientSessionImpl} in
 * the {@code org.apache.activemq.artemis.core.client.impl} package!)
 *
 * Probably the Artemis designers envisioned clients would adopt thread-scoped
 * sessions or similar confinement strategies, which is a good thing in general
 * since it avoids lock contention and deadlocks. But in Smuggler we embed
 * Artemis, producers, and consumers in the same process so we use this
 * synchronizer to make sure producers and consumers take turns to access a
 * shared session. (Yes, going forward it'd be best to use a confinement
 * strategy in Smuggler too!)
 *
 * So we use {@code atomically()} below when we access the session directly
 * in {@link ArtemisQConnector} or indirectly in {@link ArtemisQProducer}
 * and {@link ArtemisMessage}. (These are the only places where we need to
 * access the underlying Artemis session.) These three classes have to use
 * the <b>same</b> lock to ensure actions involving our session get executed
 * serially.
 */
public interface ArtemisSessionSynchronizer {

    /**
     * @return the Artemis session; never return {@code null}!
     */
    ClientSession session();

    /**
     * Runs the given supplier serially with respect to other actions on the
     * {@link #session() session} instance.
     * @param action the code to run.
     * @param <T> the type of the value returned by the action.
     * @return the value returned by the action.
     * @throws NullPointerException if the argument is {@code null}.
     */
    default <T> T atomically(SupplierE<T> action) {
        requireNonNull(action, "action");
        synchronized (session()) {  // (*)
            return action.get();
        }
    }
    /* (*) Null pointers. We assume the implementation will *never*
     * return null.
     */

    /**
     * Runs the given action serially with respect to other actions on the
     * {@link #session() session} instance.
     * @param action the code to run.
     * @throws NullPointerException if the argument is {@code null}.
     */
    default void atomically(ActionE action) {
        requireNonNull(action, "action");
        synchronized (session()) {  // (*)
            Exceptions.runUnchecked(action);
        }
    }
    /* (*) Null pointers. We assume the implementation will *never*
     * return null.
     */
}

package util.sequence;

import static java.util.Objects.*;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import util.object.Pair;

/**
 * Convenience methods, mainly useful for testing {@link Streams} methods.
 * @param <A> the type of the array.
 */
public class Arrayz<A> {  // avoids conflicts with JDK Arrays class.

    /**
     * Convenience conversion from varargs to array.
     * @param <T> any type.
     * @param ts the arguments.
     * @return the arguments array.
     */
    @SafeVarargs
    public static <T> T[] array(T...ts) {
        return ts;
    }
    
    /**
     * Is the given array reference {@code null} or is the array length 0?
     * @param <T> any type.
     * @param ts the array to test.
     * @return {@code true} for yes; {@code false} for no.
     */
    public static <T> boolean isNullOrZeroLength(T[] ts) {
        return ts == null || ts.length == 0;
    }
    
    /**
     * Does the given array hold any {@code null} reference?
     * @param <T> any type.
     * @param ts the array to test.
     * @return {@code true} if the array has at least length 1 and one of its
     * elements is {@code null}; {@code false} if the array is {@code null}, 
     * or has no elements, or all of its elements are not {@code null}. 
     */
    public static <T> boolean hasNulls(T[] ts) {
        if (!isNullOrZeroLength(ts)) {
            for (T t : ts) {
                if (t == null) return true;
            }
        }
        return false;
    }

    /**
     * Ensures you have an array of length one at least with no {@code null}
     * elements in it.
     * @param ts the array to test.
     * @param <T> any type.
     * @throws IllegalArgumentException if the given array is {@code null}, or
     * it has zero length, or some of its elements are {@code null}.
     */
    public static <T> void requireArray(T[] ts) {
        if (isNullOrZeroLength(ts) || hasNulls(ts)) {
            throw new IllegalArgumentException(
                "array of non-null objects of length at least 1 required.");
        }
    }
    
    /**
     * Collects the given array's elements into a list; if the array is 
     * {@code null}, then the empty list is returned. The returned list
     * is immutable.
     * @param <T> any type.
     * @param ts the array to convert.
     * @return the array's elements collected into a list.
     * @see #asMutableList(Object...) asMutableList
     */
    @SafeVarargs
    public static <T> List<T> asList(T...ts) {
        return ts == null ? Collections.emptyList() : Arrays.asList(ts);
    }
    
    /**
     * Collects the given array's elements into a list; if the array is 
     * {@code null}, then the empty list is returned but any {@code null}
     * elements will be otherwise added to the list.
     * @param <T> any type.
     * @param ts the array to convert.
     * @return the array's elements collected into a list.
     */
    @SafeVarargs
    public static <T> List<T> asMutableList(T...ts) {
        if (ts == null) return new ArrayList<>();
        
        ArrayList<T> ys = new ArrayList<>(ts.length);
        Collections.addAll(ys, ts);
        return ys;
    }
    
    /**
     * Collects the given array's elements into a stream; if the array is 
     * {@code null}, then the empty stream is returned.
     * @param <T> any type.
     * @param ts the array to convert.
     * @return the array's elements collected into a stream.
     */
    @SafeVarargs
    public static <T> Stream<T> asStream(T...ts) {
        return ts == null ? Stream.empty() : Stream.of(ts);
    }
    
    /**
     * Creates a new array of generic {@link Pair}'s.
     * @param <X> any type.
     * @param <Y> any type.
     * @param size the size of the array.
     * @return the new array.
     */
    @SuppressWarnings("unchecked")
    public static <X, Y> Pair<X, Y>[] newPairs(int size) {
        return (Pair<X, Y>[]) new Pair[size];
    }
    // works in most cases but see riddle at bottom of file...
    
    /**
     * Same as {@link Streams#zip(java.util.stream.Stream, java.util.stream.Stream)
     * Streams.zip} but operating on arrays.
     * @param <X> any type.
     * @param <Y> any type.
     * @param xs left list.
     * @param ys right list.
     * @return the zipped list.
     * @throws NullPointerException if either or both arguments are {@code null}.
     */
    public static <X, Y> Pair<X, Y>[] zip(X[] xs, Y[] ys) {
        requireNonNull(xs, "xs");
        requireNonNull(ys, "ys");
        
        int size = Math.min(xs.length, ys.length);
        Pair<X, Y>[] zs = newPairs(size);
        
        for (int k = 0; k < size; ++k) {
            zs[k] = new Pair<>(xs[k], ys[k]);
        }
        return zs;
    }
    
    /**
     * Same as {@link Streams#zipIndex(java.util.stream.Stream) Streams.zipIndex} 
     * but operating on arrays.
     * @param <X> any type.
     * @param xs input list.
     * @return the indexed list.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static <X> Pair<Integer, X>[] zipIndex(X[] xs) {
        requireNonNull(xs, "xs");
        
        Pair<Integer, X>[] zs = newPairs(xs.length);
        
        for (int k = 0; k < xs.length; ++k) {
            zs[k] = new Pair<>(k, xs[k]);
        }
        return zs;
    }
    // ya, could've used zip to avoid duplication, but this is more efficient.

    /**
     * Same as {@link Streams#pairUp(Stream)} Streams.pairUp} but operating on
     * arrays.
     * @param <X> any type.
     * @param xs input list.
     * @return the list of pairs.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static <X> Pair<X, X>[] pairUp(X[] xs) {
        requireNonNull(xs, "xs");

        int largestEvenSize = xs.length - xs.length % 2;
        Pair<X, X>[] ps = newPairs(largestEvenSize / 2 + xs.length % 2);
        for (int k = 0; k < largestEvenSize; k += 2) {
            ps[k/2] = new Pair<>(xs[k], xs[k + 1]);
        }
        if (largestEvenSize < xs.length) {
            ps[ps.length - 1] = new Pair<>(xs[xs.length - 1], null);
        }

        return ps;
    }

    /**
     * Access to instance operations that require a way to instantiate arrays.
     * Example: {@code op(Long[]::new).cycle(2, arrayOfLongs)}.
     * @param <T> any type.
     * @param generator function to create a new array of {@code T}'s given the
     * array size.
     * @return an {@code Aray} instance to call the desired method.
     * @throws NullPointerException if {@code generator} is {@code null}.
     */
    public static <T> Arrayz<T> op(IntFunction<T[]> generator) {
        return new Arrayz<>(generator);
    }
    
    private final IntFunction<A[]> generator;
    
    private Arrayz(IntFunction<A[]> generator) {
        requireNonNull(generator, "generator");
        this.generator = generator;
    }
    
    /**
     * Same as {@link Streams#cycle(int, java.util.stream.Stream) Streams.cycle}
     * but operating on arrays.
     * @param times how many times.
     * @param list input list.
     * @return the cycled list.
     * @throws NullPointerException if {@code list} is {@code null}.
     */
    public A[] cycle(int times, A[] list) {
        requireNonNull(list, "list");
        
        int size = list.length * times;
        A[] xs = generator.apply(size);
        
        for (int k = 0; k < size; ++k) {
            xs[k] = list[k % list.length];
        }
        return xs;
    }
    
    /**
     * Same as {@link Streams#inits(java.util.stream.Stream) Streams.inits}
     * but operating on arrays.
     * @param list input list.
     * @return the initial segments.
     * @throws NullPointerException if {@code list} is {@code null}.
     */
    public List<A[]> inits(A[] list) {
        requireNonNull(list, "list");
        
        int numberOfSegments = 1 + list.length;
        List<A[]> segments = new ArrayList<>(numberOfSegments);
        for(int k = 0; k < numberOfSegments; ++k) {
            A[] segment = generator.apply(k);
            System.arraycopy(list, 0, segment, 0, k);
            segments.add(segment);
        }
        return segments;
    }
    
    /**
     * Same as {@link Streams#map(BiFunction, java.util.stream.Stream) 
     * Streams.map} but operating on arrays.
     * @param <X> any type.
     * @param f the mapper.
     * @param list input list.
     * @return the mapped list.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public <X> A[] map(BiFunction<Integer, X, A> f, X[] list) {
        requireNonNull(f, "f");
        requireNonNull(list, "list");
        
        A[] mapped = generator.apply(list.length);
        for (int k = 0; k < mapped.length; ++k) {
            mapped[k] = f.apply(k, list[k]);
        }
        return mapped;
    }
    
    /**
     * Collects the elements of the given list that satisfy the specified
     * predicate. Elements are collected in the same order as they appear
     * in the input list.
     * @param p the test to decide which elements to take.
     * @param list the input list.
     * @return a new list with the elements that satisfy {@code p}.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public A[] filter(Predicate<A> p, A[] list) {
        requireNonNull(p, "p");
        requireNonNull(list, "list");
        
        ArrayList<A> filtered = new ArrayList<>(list.length);
        for (A x : list) {
            if (p.test(x)) {
                filtered.add(x);
            }
        }
        A[] result = generator.apply(filtered.size());
        return filtered.toArray(result);
    }
    
    /**
     * Same as {@link Streams#intersperse(Supplier, Stream)} Streams.intersperse}
     * but operating on arrays.
     * @param sep separator supplier.
     * @param list the input list.
     * @return the interspersed list.
     */
    public A[] intersperse(Supplier<A> sep, A[] list) {
        requireNonNull(sep, "sep");
        requireNonNull(list, "list");
        
        int q = Math.max(list.length - 1, 0);
        int sz = list.length + q;
        A[] interspersed = generator.apply(sz);
        
        if (list.length > 0) {
            int k = 0;
            for (; k < q; ++k) {
                interspersed[2*k] = list[k];
                interspersed[2*k + 1] = sep.get();
            }
            interspersed[2 * k] = list[k];    
        }

        return interspersed;
    }
    
    /**
     * Same as {@link Streams#pruneNull(List) Streams.pruneNull} but operating
     * on arrays.
     * @param list the input list.
     * @return the pruned list.
     */
    public A[] pruneNull(A[] list) {
        if (list == null) return generator.apply(0);
        return filter(Objects::nonNull, list);
    }
    
    /**
     * Same as {@link Streams#concat(Stream...) Streams.concat} but operating 
     * on arrays.
     * @param lists the input lists.
     * @return the concatenated list.
     */
    @SafeVarargs
    public final A[] concat(A[]...lists) {
        requireNonNull(lists, "lists");
        
        ArrayList<A[]> pruned = new ArrayList<>();
        int sz = 0;
        for (A[] xs : lists) {
            if (xs != null) {
                pruned.add(xs);
                sz += xs.length;
            }
        }
        
        A[] joined = generator.apply(sz);
        int startPos = 0;
        for (A[] xs : pruned) {
            System.arraycopy(xs, 0, joined, startPos, xs.length);
            startPos += xs.length;
        }
        
        return joined;
    }

    /**
     * Same as {@link Streams#unpair(Stream)} Streams.unpair} but operating on
     * arrays.
     * @param list the input list.
     * @return the unpaired list.
     * @throws NullPointerException if the argument or any of its elements is
     * {@code null}.
     */
    public A[] unpair(Pair<A, A>[] list) {
        requireNonNull(list, "list");
        A[] flattened = generator.apply(list.length * 2);

        for (int k = 0; k < list.length; ++k) {
            requireNonNull(list[k], "null pair");

            flattened[2*k] = list[k].fst();
            flattened[2*k + 1] = list[k].snd();
        }
        return flattened;
    }
    
}
/* So here's a riddle:
 * 
    @SuppressWarnings("unchecked")
    public static <X, Y>
    Pair<X, Y>[] why(Stream<Pair<X, Y>> ps) {
        Pair<X, Y>[] path = ps.toArray(Arrayz::newPairs);       // newPairs works here
                             
        return Arrayz //.op(Arrayz::newPairs)                   // but not here!
                        .op(sz -> (Pair<X, Y>[]) new Pair[sz])  // inlining works!
                        .map((i, p) -> p, path);
    }
 *
 * Arrayz::newPairs works in the first call but wouldn't work in the second. 
 * Moreover, the type of map inferred by the compiler seems to be (note the
 * nested Pair):
 * 
 * <Pair<X, Y>> Pair<Pair<X, Y>, Y>[] 
 * Arrayz.map(BiFunction<Integer, Pair<X, Y>, Pair<Pair<X, Y>, Y>> f, Pair<X, Y>[] list)
 * 
 * But then why the inferred type for map in this case:
 *
    @SuppressWarnings("unchecked")
    Pair<Long, Byte>[] why1(Stream<Pair<Long, Byte>> ps) {
        Pair<Long, Byte>[] path = ps.toArray(Arrayz::newPairs);
                             
        return Arrayz //.op(Arrayz::newPairs)
                        .op(sz -> (Pair<Long, Byte>[]) new Pair[sz])
                        .map((i, p) -> p, path);
    }
 *
 * seems to be (no nested Pair):
 * 
 * <Pair<Long, Byte>> Pair<Long, Byte>[] 
 * Arrayz.map(BiFunction<Integer, Pair<Long, Byte>, Pair<Long, Byte>> f, Pair<Long, Byte>[] list)
 * 
 * For example look at: StreamsMapTest.mapWithPairIsZip(). 
 * What the hell is going on?  
 * If somebody could explain it to me I'll be forever grateful. 
 */
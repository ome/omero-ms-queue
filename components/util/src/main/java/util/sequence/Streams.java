package util.sequence;

import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.empty;
import static java.util.stream.Stream.iterate;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import util.object.Pair;

/**
 * Utility methods to use with {@link Stream}'s.
 * Similar in concept to the Haskell list processing functions but with all
 * the (ridiculous?) limitations of Java. 
 * <em>Do not use for heavy duty or parallel computations</em>. You have been
 * warned...
 */
public class Streams {
    
    // NOTE: these methods all have naive implementations, you're welcome to 
    // replace them with something better; just beware of the ridiculous amount
    // of work involved---e.g. performance, mutation, parallel vs sequential, 
    // ordered vs unordered, etc. Love Java.

    /**
     * Repeats a <em>finite</em> stream the specified number of {@code times}.
     * This is a terminal operation on the input stream.
     * @param <T> any type.
     * @param times how many times to repeat the input stream.
     * @param ts the input stream.
     * @return the stream {@code ts + ts + ... + ts}, the given number of 
     * {@code times}.
     * @throws NullPointerException if any argument is {@code null}. 
     */
    public static <T> Stream<T> cycle(int times, Stream<T> ts) {
        requireNonNull(ts, "ts");
        
        List<T> xs = ts.collect(toList());
        return iterate(xs.stream(), i -> xs.stream())
               .limit(times < 0 ? 0 : times)
               .reduce(empty(), (x, y) -> concat(x, y));
    }
    
    /**
     * Builds the initial segments of the given <em>finite</em> input stream
     * {@code ts}.
     * For example, if {@code ts = [1, 2, 3]} then {@code init(ts) = [[], [1], 
     * [1,2], [1,2,3]]}.
     * This is a terminal operation.
     * @param <T> any type.
     * @param ts the input stream.
     * @return the initial segments of {@code ts}.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static <T> Stream<Stream<T>> inits(Stream<T> ts) {
        requireNonNull(ts, "ts");
        
        List<T> xs = ts.collect(toList());
        Pair<Stream<T>, Long> seed = new Pair<>(empty(), 0L);
        
        UnaryOperator<Pair<Stream<T>, Long>> nextSegment = k -> {
            long size = k.snd() + 1;
            Stream<T> segment = xs.stream().limit(size);
            return new Pair<>(segment, size);
        };
        return iterate(seed, nextSegment)
               .map(Pair::fst)
               .limit(1 + xs.size());
    }
    
    /**
     * Maps the function {@code f} over the list of pairs (x<sub>0</sub>, 
     * y<sub>0</sub>), (x<sub>1</sub>, y<sub>1</sub>), ... , (x<sub>m</sub>, 
     * y<sub>m</sub>) with x<sub>k</sub> in {@code xs} and y<sub>k</sub> in
     * {@code ys} and where {@code m} is the length of the shortest between
     * {@code xs} and {@code ys}.
     * For example (pseudo code): {@code zipWith((x,y) -> x + y, [a,b], [1,2,3])
     * = [a1, b2]}.
     * This is a terminal operation on the input streams.
     * @param <X> any type.
     * @param <Y> any type.
     * @param <Z> any type.
     * @param f the function to map.
     * @param xs the list providing the left values.
     * @param ys the list providing the right values.
     * @return the "zipped" list.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static <X, Y, Z>
    Stream<Z> zipWith(BiFunction<X, Y, Z> f, Stream<X> xs, Stream<Y> ys) {
        requireNonNull(f, "f");
        requireNonNull(xs, "xs");
        requireNonNull(ys, "ys");
        
        Iterator<X> ix = xs.iterator();
        Iterator<Y> iy = ys.iterator();
        List<Z> zs = new ArrayList<>();
        
        while (ix.hasNext() && iy.hasNext()) {
            zs.add(f.apply(ix.next(), iy.next()));
        }
        
        return zs.stream();
    }
    
    /**
     * Pairs up the elements of two lists up to the length of the shortest of
     * the two.
     * For example (pseudo code): {@code zip([a,b], [1,2,3]) = [(a, 1), (b, 2)]}.
     * This is a terminal operation on the input streams.
     * @param <X> any type.
     * @param <Y> any type.
     * @param xs the list providing the left values.
     * @param ys the list providing the right values.
     * @return the "zipped" list.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static <X, Y> Stream<Pair<X, Y>> zip(Stream<X> xs, Stream<Y> ys) {
        return zipWith(Pair::new, xs, ys);
    }
    
    /**
     * Indexes the elements of the given finite list, starting from {@code 0}.
     * For example (pseudo code): {@code zipIndex([a,b,c]) = [(a, 0), (b, 1),
     * (c, 2)]}.
     * This is a terminal operation on the input streams.
     * @param <X> any type.
     * @param xs the list to index.
     * @return the indexed list.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static <X> Stream<Pair<Integer, X>> zipIndex(Stream<X> xs) {
        Stream<Integer> ks = Stream.iterate(0, x -> x + 1);
        return zip(ks, xs);
    }
    
    /**
     * Applies the function {@code f} to each element of the stream.
     * The function {@code f} is called with the index (in {@code ys}) of the
     * element to map (first argument) and with the element itself (second
     * argument). That is: {@code map(f,[v,w,...]) = [f(0,v),f(1,w),...]}.
     * For example (pseudo code) if {@code f(i,x) = i+x} then 
     * {@code map(f,[1,2,3]) = [1,3,5]}.
     * @param <Y> any type.
     * @param <Z> any type.
     * @param f turns an index and a {@code Y} into a {@code Z}.
     * @param ys the list to map.
     * @return a new list with the mapped elements.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static <Y, Z>
    Stream<Z> map(BiFunction<Integer, Y, Z> f, Stream<Y> ys) {
        requireNonNull(f, "f");
        requireNonNull(ys, "ys");
        
        Iterator<Y> iy = ys.iterator();
        List<Z> zs = new ArrayList<>();
        
        int index = 0;
        while (iy.hasNext()) {
            zs.add(f.apply(index++, iy.next()));
        }
        
        return zs.stream();
    }
    // ya, could be done using zipWith (e.g. map f = zipWith f [0..]) but the
    // implementation is more complicated and has worse performance than the 
    // code above!
    
    /**
     * Interleaves the elements of the given stream with the specified 
     * separator.
     * For example (pseudo code) {@code intersperse(0,[1,2,3]) = [1,0,2,0,3]}.
     * @param <X> any type.
     * @param sep the value to put in between stream elements; ideally this 
     * should be a new object for each slot.
     * @param xs the elements to interleave.
     * @return the interleaved list.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static <X> Stream<X> intersperse(Supplier<X> sep, Stream<X> xs) {
        requireNonNull(sep, "sep");
        requireNonNull(xs, "xs");
        
        return xs.flatMap(x -> Stream.of(sep.get(), x)).skip(1);
    }
    
    /**
     * Removes all {@code null}'s from the stream; if the stream itself is
     * {@code null}, then the empty stream is returned.
     * @param <T> any type.
     * @param xs the stream to cleanse.
     * @return the cleansed stream.
     */
    public static <T> Stream<T> pruneNull(Stream<T> xs) {
        if (xs == null) return empty();
        return xs.filter(Objects::nonNull);
    }
    
    /**
     * Removes all {@code null}'s from the list; if the list itself is
     * {@code null}, then the empty stream is returned.
     * @param <T> any type.
     * @param xs the list to cleanse.
     * @return the cleansed stream.
     */
    public static <T> Stream<T> pruneNull(List<T> xs) {
        if (xs == null) return empty();
        return pruneNull(xs.stream());
    }
    
    /**
     * Removes all {@code null}'s from the array; if the array itself is
     * {@code null}, then the empty stream is returned.
     * @param <T> any type.
     * @param xs the array to cleanse.
     * @return the cleansed stream.
     */
    public static <T> Stream<T> pruneNull(T[] xs) {
        if (xs == null) return empty();
        return pruneNull(Arrays.stream(xs));
    }
    
    /**
     * Collects the given stream's elements into a list; if the stream is 
     * {@code null}, then the empty list is returned.
     * @param <T> any type.
     * @param xs the stream to convert.
     * @return the stream's elements collected into a list.
     */
    public static <T> List<T> asList(Stream<T> xs) {
        if (xs == null) return Collections.emptyList();
        return xs.collect(toList());
    }
    
    /**
     * Collects the given list's elements into a stream; if the list is {@code
     * null}, then the empty stream is returned.
     * @param <T> any type.
     * @param xs the list to convert.
     * @return the list's elements collected into a stream.
     */
    public static <T> Stream<T> asStream(List<T> xs) {
        return xs == null ? Stream.empty() : xs.stream();
    }
    
    /**
     * Puts an optional value into a stream.
     * If the given value is {@code null} or an empty optional, then the empty
     * stream is returned; otherwise a stream containing the optional value.
     * @param <T> any type.
     * @param maybeValue the value to convert, possibly {@code null}.
     * @return an empty stream or a stream with the optional value as the case
     * may be.
     */
    public static <T> Stream<T> asStream(Optional<T> maybeValue) {
        return maybeValue != null && maybeValue.isPresent() ?
                Stream.of(maybeValue.get()) : Stream.empty();
    }
    
    /**
     * Returns empty if the argument is {@code null}, otherwise the argument
     * itself.
     * @param <T> any type.
     * @param xs a possibly {@code null} reference.
     * @return {@code xs} if not {@code null}, an empty stream otherwise. 
     */
    public static <T> Stream<T> emptyIfNull(Stream<T> xs) {
        return xs == null ? Stream.empty() : xs;
    }
    
    /**
     * Joins the given streams in encounter order.
     * For example (pseudo code): {@code concat([1, 2], null, [3, 4]) = [1, 2, 
     * 3, 4]}. Any {@code null} stream is replaced with empty.
     * @param <T> any type.
     * @param xs the streams to join.
     * @return the joined stream.
     * @throws NullPointerException if the argument array is {@code null}.
     */
    @SafeVarargs
    public static <T> Stream<T> concat(Stream<T>...xs) {
        requireNonNull(xs, "xs");
        return Stream.of(xs).flatMap(identity());
    }

    /**
     * Collects the items of the given <em>finite</em> stream into pairs.
     * Items are paired up in encounter order and if there's an odd number of
     * them, the second element of the last pair will be {@code null}.
     * For example (pseudo code): {@code pairUp [1, null, 3, 4, 5] = [
     * (1, null), (3, 4), (5, null)]}. (Note that any {@code null} input item
     * will be put in the right place, no exception is thrown.)
     * @param <T> any type.
     * @param xs the input items.
     * @return the input items collected into pairs.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static <T> Stream<Pair<T,T>> pairUp(Stream<T> xs) {
        requireNonNull(xs, "xs");

        List<Pair<T, T>> ps = new ArrayList<>();
        List<T> ts = asList(xs);
        int largestEvenSize = ts.size() - ts.size() % 2;
        for (int k = 0; k < largestEvenSize; k += 2) {
            Pair<T, T> p = new Pair<>(ts.get(k), ts.get(k + 1));
            ps.add(p);
        }
        if (largestEvenSize < ts.size()) {
            ps.add(new Pair<>(ts.get(largestEvenSize), null));
        }
        return ps.stream();
    }

    /**
     * Flattens the given stream of pairs in encounter order.
     * For example (pseudo code): {@code unpair [(1, null), (3, 4), (5, null)] =
     * [1, null, 3, 4, 5, null]}. Note that if the stream contains a {@code
     * null} {@link Pair}, then an exception is thrown. However, any {@code
     * null} within a {@link Pair} will be put in the right place, no exception
     * is thrown.
     * @param <T> any type.
     * @param xs the input pairs.
     * @return the flattened stream.
     * @throws NullPointerException if the argument or any of its pair objects
     * is {@code null}.
     */
    public static <T> Stream<T> unpair(Stream<Pair<T, T>> xs) {
        requireNonNull(xs, "xs");

        ArrayList<T> ys = new ArrayList<>();
        xs.forEach(p -> {
            requireNonNull(p, "null pair");

            ys.add(p.fst());
            ys.add(p.snd());
        });
        return ys.stream();
    }
    
}

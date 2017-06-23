package util.alg;

import static java.util.Objects.requireNonNull;

import java.util.stream.Stream;

/**
 * Operations on monoids.
 */
public class Monoids<T> {

    /**
     * Syntactic sugar to use in place of the {@link #Monoids(Monoid) 
     * constructor}  if you like, e.g.
     * <pre>
     *   Monoids.with(someMonoid)
     *          .fold(streamOfStream)
     * </pre>
     * @param <T> elements type.
     * @param monoid the monoid to use.
     * @return a new {@code Monoids} instance.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static <T> Monoids<T> with(Monoid<T> monoid) {
        return new Monoids<>(monoid);
    }
    
    private final Monoid<T> monoid;
    
    /**
     * Creates a new instance to carry out operations on the specified monoid.
     * @param monoid the monoid to use.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public Monoids(Monoid<T> monoid) {
        requireNonNull(monoid, "monoid");
        this.monoid = monoid;
    }
    
    /**
     * "Multiplies" the given sequence of objects.
     * This is the canonical reduction operation on the free monoid, see e.g. 
     * <a href="https://en.wikipedia.org/wiki/Monoid#Monoids_in_computer_science">
     * monoids in computer science</a>.
     * @param ts the values to multiply.
     * @return the result of multiplying the stream of values.
     * @throws NullPointerException if the argument is {@code null} or any of
     * the contained objects is {@code null}.
     */
    public T fold(Stream<T> ts) {
        requireNonNull(ts, "ts");
        return ts.reduce(monoid.unit(), monoid::multiply);
    }
    
}

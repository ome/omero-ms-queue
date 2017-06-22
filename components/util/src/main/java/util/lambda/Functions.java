package util.lambda;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

/**
 * Lambda utilities.
 */
public class Functions {

    /**
     * The constant function {@code X → K: x ↦ c }.
     * It discards its input and always returns the given constant {@code c}.
     * @param <X> domain type.
     * @param <K> codomain type.
     * @param c the constant value to return.
     * @return the constant function {@code x ↦ c }.
     */
    public static <X, K> Function<X, K> constant(K c) {
        return x -> c;
    }

    /**
     * Applies a function to an argument to produce a result.
     * @param <X> domain type.
     * @param <Y> codomain type.
     * @param f the function.
     * @param x the argument.
     * @return {@code f} of {@code x}.
     * @throws NullPointerException if the function argument is {@code null}.
     */
    public static <X, Y> Y apply(Function<X, Y> f, X x) {
        requireNonNull(f, "f");
        return f.apply(x);
    }

}

package ome.smuggler.core.types;

import java.util.Optional;
import java.util.function.Predicate;

import util.object.Wrapper;

/**
 * A positive natural number, i.e. an integer greater than zero.
 */
public class PositiveN extends Wrapper<Long> {

    /**
     * Test to check if a {@code long} can be used to instantiate a
     * {@link PositiveN}.
     */
    public static final Predicate<Long> isValid = x -> x > 0;

    /**
     * Instantiate a {@link PositiveN} from the given value only if it's
     * positive.
     * @param value the value to use.
     * @return a {@link PositiveN} wrapping the given value or empty if the
     * value isn't positive.
     */
    public static Optional<PositiveN> from(long value) {
        return isValid.test(value) ? Optional.of(new PositiveN(value))
                                   : Optional.empty();
    }

    /**
     * Instantiate a {@link PositiveN} from a positive value.
     * @param positiveValue the value to use.
     * @return a {@link PositiveN} wrapping the given value.
     * @throws IllegalArgumentException if the value isn't positive.
     */
    public static PositiveN of(long positiveValue) {
        return from(positiveValue)
              .orElseThrow(() -> new IllegalArgumentException(
                            "non-positive value: " + positiveValue));
    }

    protected PositiveN(Long wrappedValue) {
        super(wrappedValue);
    }

}
/* Design Debt.
 * Nat and PositiveN duplicate code. They can be refactored using a generic
 * value-builder with `from` and `of` methods.
 */

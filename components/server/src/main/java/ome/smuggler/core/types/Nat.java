package ome.smuggler.core.types;

import java.util.Optional;
import java.util.function.Predicate;

import util.object.Wrapper;

/**
 * A natural number, i.e. an integer greater than or equal to zero.
 */
public class Nat extends Wrapper<Long> {

    /**
     * Test to check if a {@code long} can be used to instantiate a {@link Nat}.
     */
    public static final Predicate<Long> isValid = x -> x >= 0;
    
    /**
     * Instantiate a {@link Nat} from the given value only if it's not negative.
     * @param value the value to use.
     * @return a {@link Nat} wrapping the given value or empty if the value is
     * negative.
     */
    public static Optional<Nat> from(long value) {
        return isValid.test(value) ? Optional.of(new Nat(value)) 
                                   : Optional.empty();
    }
    
    /**
     * Instantiate a {@link Nat} from a non-negative value.
     * @param nonNegativeValue the value to use.
     * @return a {@link Nat} wrapping the given value.
     * @throws IllegalArgumentException if the value is negative.
     */
    public static Nat of(long nonNegativeValue) {
        return from(nonNegativeValue)
               .orElseThrow(() -> new IllegalArgumentException(
                                       "negative value: " + nonNegativeValue)); 
    }
    
    private Nat(Long wrappedValue) {
        super(wrappedValue);
    }

}

package util.validation;

import util.object.Either;

/**
 * A function that validates {@code T}-values.
 */
public interface Validator<E, T> {
    
    /**
     * Tells if the given value is a valid one.
     * @param value the value to test.
     * @return a left value if validation fails, a right value containing the
     * input as is if validation succeeds.
     */
    Either<E, T> validate(T value);
    
}

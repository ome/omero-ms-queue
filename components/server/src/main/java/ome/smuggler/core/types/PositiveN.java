package ome.smuggler.core.types;

import static util.error.Exceptions.getOrThrow;

import util.object.Wrapper;

/**
 * A positive natural number, i.e. an integer greater than zero.
 * An instance of this object can only be obtained through a parser so that it
 * is impossible to construct an invalid value.
 */
public class PositiveN extends Wrapper<Long> {

    public static PositiveN of(long positiveValue) {
        return getOrThrow(
                    ValueParserFactory
                    .positiveInt(String.valueOf(positiveValue))
                    .mapLeft(IllegalArgumentException::new)
               );
    }
    
    protected PositiveN(Long wrappedValue) {
        super(wrappedValue);
    }

}

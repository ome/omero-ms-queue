package ome.smuggler.core.types;

/**
 * An OME screen ID.
 * An instance of this object can only be obtained through a parser so that it
 * is impossible to construct an invalid value.
 */
public class ScreenId extends PositiveN {

    protected ScreenId(Long wrappedValue) {
        super(wrappedValue);
    }

}
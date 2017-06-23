package ome.smuggler.core.types;

/**
 * An OME dataset ID.
 * An instance of this object can only be obtained through a parser so that it
 * is impossible to construct an invalid value.
 */
public class DatasetId extends PositiveN {

    protected DatasetId(Long wrappedValue) {
        super(wrappedValue);
    }

}

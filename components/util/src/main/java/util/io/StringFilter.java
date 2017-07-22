package util.io;

import java.util.function.Function;

import util.string.Strings;


/**
 * A specialised {@link ValueFilter} that reads and writes strings.
 */
public class StringFilter extends ValueFilter<String> {

    /**
     * Creates a new instance.
     * @param setter given the deserialised value, produces the value to
     *               serialise to the output stream.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public StringFilter(Function<String, String> setter) {
        super(StreamOps::readLinesIntoString, Strings::write, setter);
    }

}

package util.serialization.json;

import static java.util.Objects.requireNonNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


/**
 * A {@link JsonStreamReader} that de-serialises from an {@link InputStream}.
 * @see JsonOutputStreamWriter
 */
public class JsonInputStreamReader<T>
        extends JsonStreamReader<InputStream, T> {

    private static Reader toReader(InputStream in, Charset encoding) {
        requireNonNull(in, "in");
        requireNonNull(encoding, "encoding");

        Reader r = new InputStreamReader(in, encoding);
        return new BufferedReader(r);
    }

    /**
     * Creates a new instance.
     * @param valueType the class of the object to read.
     * @param encoding which character set to use to read the serialised JSON
     *                 values from the input stream.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public JsonInputStreamReader(Class<T> valueType, Charset encoding) {
        super(valueType, in -> toReader(in, encoding));
    }

    /**
     * Creates a new instance.
     * JSON will read as a UTF-8 from the stream.
     * @param valueType the class of the object to read.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public JsonInputStreamReader(Class<T> valueType) {
        this(valueType, StandardCharsets.UTF_8);
    }

}

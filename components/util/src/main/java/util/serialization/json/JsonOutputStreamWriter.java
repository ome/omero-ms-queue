package util.serialization.json;

import static java.util.Objects.requireNonNull;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


/**
 * A {@link JsonStreamWriter} that serialises to an {@link OutputStream}.
 * @see JsonInputStreamReader
 */
public class JsonOutputStreamWriter<T>
        extends JsonStreamWriter<T, OutputStream> {

    private static Writer toWriter(OutputStream out, Charset encoding) {
        requireNonNull(out, "out");
        requireNonNull(encoding, "encoding");

        OutputStreamWriter w = new OutputStreamWriter(out, encoding);
        return new BufferedWriter(w);
    }

    /**
     * Creates a new instance.
     * Serialised JSON values will be encoded in the output stream using UTF-8.
     */
    public JsonOutputStreamWriter() {
        this(StandardCharsets.UTF_8);
    }

    /**
     * Creates a new instance.
     * @param encoding which character set to use to encode serialised JSON
     *                 values in the output stream.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public JsonOutputStreamWriter(Charset encoding) {
        super(out -> toWriter(out, encoding));
    }

}

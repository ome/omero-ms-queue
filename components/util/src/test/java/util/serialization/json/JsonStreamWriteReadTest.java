package util.serialization.json;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class JsonStreamWriteReadTest {

    @SuppressWarnings("unchecked")
    public static <T> T writeThenRead(T value) {
        JsonOutputStreamWriter<T> writer = new JsonOutputStreamWriter<>();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writer.uncheckedWrite(out, value);

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        JsonInputStreamReader<T> reader =
                new JsonInputStreamReader<>((Class<T>)value.getClass());
        return reader.uncheckedRead(in);
    }


    private int x;
    private String y;

    @Test
    public void canWriteAndReadJson() {
        JsonStreamWriteReadTest value = new JsonStreamWriteReadTest();
        value.x = 123;
        value.y = "some \n unicode ∞ for you!";

        JsonStreamWriteReadTest deserialised = writeThenRead(value);

        assertNotNull(deserialised);
        assertThat(deserialised.x, is(value.x));
        assertThat(deserialised.y, is(value.y));
    }

}

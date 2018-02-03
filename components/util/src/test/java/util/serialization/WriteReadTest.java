package util.serialization;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.*;

import util.io.SinkWriter;
import util.io.SourceReader;


public abstract class WriteReadTest {

    protected ByteArrayOutputStream serializedData;

    protected abstract SerializationFactory factory();

    protected <T> void write(T valueToWrite) {
        serializedData = new ByteArrayOutputStream();
        SinkWriter<T, OutputStream> writer = factory().serializer();

        writer.uncheckedWrite(serializedData, valueToWrite);
    }

    protected <T> T read(Class<T> valueType) {
        ByteArrayInputStream source =
                new ByteArrayInputStream(serializedData.toByteArray());
        SourceReader<InputStream, T> reader = factory().deserializer(valueType);

        return reader.uncheckedRead(source);
    }

    protected <T> T writeThenRead(T valueToWrite, Class<T> valueType) {
        write(valueToWrite);
        return read(valueType);
    }

    protected <T> void assertWriteThenReadGivesInitialValue(
            T initialValue, Class<T> valueType) {
        T readValue = writeThenRead(initialValue, valueType);
        assertThat(readValue, is(initialValue));
    }

}

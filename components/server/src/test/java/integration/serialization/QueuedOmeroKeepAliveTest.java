package integration.serialization;

import static ome.smuggler.core.types.FutureTimepoint.now;

import com.google.gson.reflect.TypeToken;
import org.junit.Test;

import ome.smuggler.core.types.QueuedOmeroKeepAlive;

import java.net.URI;

public class QueuedOmeroKeepAliveTest extends JsonWriteReadTest {

    @Test
    @SuppressWarnings("unchecked")
    public void serializeAndDeserialize() {
        QueuedOmeroKeepAlive initialValue =
                new QueuedOmeroKeepAlive(URI.create("h:1"), "sesh", now());
        Class<QueuedOmeroKeepAlive> valueType = (Class<QueuedOmeroKeepAlive>)
                initialValue.getClass();
        TypeToken<QueuedOmeroKeepAlive> typeToken =
                new TypeToken<QueuedOmeroKeepAlive>() {
                };

        assertWriteThenReadGivesInitialValue(initialValue, valueType);
        assertWriteThenReadGivesInitialValue(initialValue, typeToken);
    }

}
package util.serialization.json;

import org.junit.Test;

public class PrimitiveTypesTest extends JsonWriteReadTest {
    
    @Test
    public void serializeAndDeserializeString() {
        String initialValue = "1";
        Class<String> valueType = String.class;
        assertWriteThenReadGivesInitialValue(initialValue, valueType);
    }

    @Test
    public void serializeAndDeserializeInteger() {
        assertWriteThenReadGivesInitialValue(1, int.class);
    }

    @Test
    public void serializeAndDeserializeLong() {
        assertWriteThenReadGivesInitialValue(1L, long.class);
    }

    @Test
    public void serializeAndDeserializeDouble() {
        assertWriteThenReadGivesInitialValue(1.23, double.class);
    }

}

package integration.serialization;

import java.util.Optional;

import org.junit.Test;

import com.google.gson.reflect.TypeToken;

public class OptionalStringTest extends JsonWriteReadTest {
    
    @SuppressWarnings("unchecked")
    private void assertSerializeAndDeserialize(Optional<String> initialValue) {
        Class<Optional<String>> valueType = (Class<Optional<String>>) 
                initialValue.getClass();
        TypeToken<Optional<String>> typeToken = 
                new TypeToken<Optional<String>>(){}; 
        
        assertWriteThenReadGivesInitialValue(initialValue, valueType);
        assertWriteThenReadGivesInitialValue(initialValue, typeToken);
    }
    
    @Test
    public void serializeAndDeserializeValue() throws Exception {
        assertSerializeAndDeserialize(Optional.of("xxx"));
    }

    @Test
    public void serializeAndDeserializeEmpty() {
        assertSerializeAndDeserialize(Optional.empty());
    }
    
}

package integration.serialization;

import static ome.smuggler.core.types.ValueParserFactory.screenId;

import org.junit.Test;

import com.google.gson.reflect.TypeToken;

import ome.smuggler.core.types.ScreenId;

public class ScreenIdTest extends JsonWriteReadTest {
    
    @Test
    @SuppressWarnings("unchecked")
    public void jsonSerializeAndDeserialize() throws Exception {
        ScreenId initialValue = screenId("321").getRight();
        Class<ScreenId> valueType = (Class<ScreenId>) initialValue.getClass();
        TypeToken<ScreenId> typeToken = new TypeToken<ScreenId>(){}; 
        
        assertWriteThenReadGivesInitialValue(initialValue, valueType);
        assertWriteThenReadGivesInitialValue(initialValue, typeToken);
    }

}

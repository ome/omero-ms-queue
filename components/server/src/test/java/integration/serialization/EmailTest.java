package integration.serialization;

import static ome.smuggler.core.types.ValueParserFactory.email;
import ome.smuggler.core.types.Email;

import org.junit.Test;

import com.google.gson.reflect.TypeToken;

public class EmailTest extends JsonWriteReadTest {
    
    @Test
    @SuppressWarnings("unchecked")
    public void jsonSerializeAndDeserialize() throws Exception {
        Email initialValue = email("guy@fbi.edu").getRight();
        Class<Email> valueType = (Class<Email>) initialValue.getClass();
        TypeToken<Email> typeToken = new TypeToken<Email>(){}; 
        
        assertWriteThenReadGivesInitialValue(initialValue, valueType);
        assertWriteThenReadGivesInitialValue(initialValue, typeToken);
    }
    
}

package integration.serialization;

import static ome.smuggler.core.types.ValueParserFactory.email;

import org.junit.Test;

import com.google.gson.reflect.TypeToken;

import ome.smuggler.core.types.Email;
import ome.smuggler.core.types.PlainTextMail;

public class PlainTextMailTest extends JsonWriteReadTest {
    
    @Test
    @SuppressWarnings("unchecked")
    public void jsonSerializeAndDeserialize() throws Exception {
        Email recipient = email("guy@fbi.edu").getRight();
        PlainTextMail initialValue = 
                new PlainTextMail(recipient, "title", "content"); 
        Class<PlainTextMail> valueType = (Class<PlainTextMail>) 
                initialValue.getClass();
        TypeToken<PlainTextMail> typeToken = 
                new TypeToken<PlainTextMail>(){}; 
        
        assertWriteThenReadGivesInitialValue(initialValue, valueType);
        assertWriteThenReadGivesInitialValue(initialValue, typeToken);
    }
    
}

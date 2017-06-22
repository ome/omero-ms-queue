package integration.serialization;

import org.junit.Test;

import com.google.gson.reflect.TypeToken;

import ome.smuggler.core.types.MailId;

public class MailIdTest extends JsonWriteReadTest {
    
    @Test
    public void jsonSerializeAndDeserialize() {
        MailId initialValue = new MailId();
        
        assertWriteThenReadGivesInitialValue(initialValue, MailId.class);
        assertWriteThenReadGivesInitialValue(initialValue, new TypeToken<MailId>(){});
    }
    
}

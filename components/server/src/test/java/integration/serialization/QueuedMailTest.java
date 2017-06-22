package integration.serialization;

import static ome.smuggler.core.types.ValueParserFactory.email;

import org.junit.Test;

import com.google.gson.reflect.TypeToken;

import ome.smuggler.core.types.Email;
import ome.smuggler.core.types.MailId;
import ome.smuggler.core.types.PlainTextMail;
import ome.smuggler.core.types.QueuedMail;

public class QueuedMailTest extends JsonWriteReadTest {

    @Test
    @SuppressWarnings("unchecked")
    public void serializeAndDeserialize() {
        Email recipient = email("guy@fbi.edu").getRight();
        PlainTextMail msg = new PlainTextMail(recipient, "title", "content");
        QueuedMail initialValue = new QueuedMail(new MailId(), msg);
        Class<QueuedMail> valueType = (Class<QueuedMail>) 
                                        initialValue.getClass();
        TypeToken<QueuedMail> typeToken = new TypeToken<QueuedMail>(){}; 
        
        assertWriteThenReadGivesInitialValue(initialValue, valueType);
        assertWriteThenReadGivesInitialValue(initialValue, typeToken);
    }
    
}

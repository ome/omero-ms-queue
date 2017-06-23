package integration.serialization;

import static ome.smuggler.core.types.ValueParserFactory.*;

import java.net.URI;

import org.junit.Test;

import com.google.gson.reflect.TypeToken;

public class UriTest  extends JsonWriteReadTest {
    
    @SuppressWarnings("unchecked")
    private void assertSerializeAndDeserialize(URI initialValue) {
        Class<URI> valueType = (Class<URI>) initialValue.getClass();
        TypeToken<URI> typeToken = new TypeToken<URI>(){}; 
        
        assertWriteThenReadGivesInitialValue(initialValue, valueType);
        assertWriteThenReadGivesInitialValue(initialValue, typeToken);
    }
    
    @Test
    public void serializeAndDeserialize() throws Exception {
        assertSerializeAndDeserialize(uri("http://host/path/").getRight());
    }
    
    @Test
    public void serializeAndDeserializeOmeroUri() throws Exception {
        assertSerializeAndDeserialize(omeroUri("omero", "1234").getRight());
    }
    
}
package integration.serialization;

import static ome.smuggler.core.types.ValueParserFactory.*;

import ome.smuggler.core.types.ImportInput;
import util.types.PositiveN;
import ome.smuggler.core.types.TextAnnotation;

import org.junit.Test;

import com.google.gson.reflect.TypeToken;


public class ImportInputTest extends JsonWriteReadTest {

    private static PositiveN posN(Integer value) {
        return positiveInt(value.toString()).getRight();
    }
    
    private TextAnnotation anno(String ns, String value) {
        return textAnnotation(ns, value).getRight();
    }
    
    public static ImportInput makeNew() {
        return new ImportInput(email("user@micro.edu").getRight(), 
                               uri("target/file").getRight(), 
                               uri("omero:1234").getRight(), 
                               "sessionKey");
    }
    
    @SuppressWarnings("unchecked")
    private void assertSerializeAndDeserialize(ImportInput initialValue) {
        Class<ImportInput> valueType = (Class<ImportInput>) initialValue.getClass();
        TypeToken<ImportInput> typeToken = new TypeToken<ImportInput>(){}; 
        
        assertWriteThenReadGivesInitialValue(initialValue, valueType);
        assertWriteThenReadGivesInitialValue(initialValue, typeToken);
    }
    
    @Test
    public void serializeAndDeserializeOnlyRequiredFields() {
        ImportInput initialValue = makeNew();
        assertSerializeAndDeserialize(initialValue);
    }
    
    @Test
    public void serializeAndDeserializeWithName() {
        ImportInput initialValue = makeNew().setName("n");
        assertSerializeAndDeserialize(initialValue);
    }
    
    @Test
    public void serializeAndDeserializeWithDescription() {
        ImportInput initialValue = makeNew().setName("n").setDescription("d");
        assertSerializeAndDeserialize(initialValue);
    }
    
    @Test
    public void serializeAndDeserializeWithScreenId() {
        ImportInput initialValue = makeNew()
                                  .setName("n")
                                  .setDescription("d")
                                  .setScreenId(posN(1));
        assertSerializeAndDeserialize(initialValue);
    }
    
    @Test
    public void serializeAndDeserializeWithAnnotations() {
        ImportInput initialValue = makeNew()
                                  .setName("n")
                                  .setDescription("d")
                                  .setScreenId(posN(1))
                                  .addTextAnnotation(anno("n", "t"));
        assertSerializeAndDeserialize(initialValue);
    }
    
    @Test
    public void serializeAndDeserializeWithAllFieldsPresent() {
        ImportInput initialValue = makeNew()
                                  .setName("n")
                                  .setDescription("d")
                                  .setScreenId(posN(1))
                                  .addTextAnnotation(anno("n", "t"))
                                  .addAnnotationId(posN(2));
        assertSerializeAndDeserialize(initialValue);
    }
    
}

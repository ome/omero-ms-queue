package integration.serialization;

import static ome.smuggler.core.types.ValueParserFactory.datasetId;

import org.junit.Test;

import com.google.gson.reflect.TypeToken;

import ome.smuggler.core.types.DatasetId;

public class DatasetIdTest extends JsonWriteReadTest {
    
    @Test
    @SuppressWarnings("unchecked")
    public void jsonSerializeAndDeserialize() throws Exception {
        DatasetId initialValue = datasetId("321").getRight();
        Class<DatasetId> valueType = (Class<DatasetId>) initialValue.getClass();
        TypeToken<DatasetId> typeToken = new TypeToken<DatasetId>(){}; 
        
        assertWriteThenReadGivesInitialValue(initialValue, valueType);
        assertWriteThenReadGivesInitialValue(initialValue, typeToken);
    }
    
}

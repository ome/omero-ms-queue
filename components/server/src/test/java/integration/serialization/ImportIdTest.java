package integration.serialization;

import ome.smuggler.core.types.ImportBatchId;
import ome.smuggler.core.types.ImportId;

import org.junit.Test;

import com.google.gson.reflect.TypeToken;

public class ImportIdTest extends JsonWriteReadTest {
    
    @Test
    public void jsonSerializeAndDeserialize() {
        ImportId initialValue = new ImportId(new ImportBatchId());
        
        assertWriteThenReadGivesInitialValue(initialValue, ImportId.class);
        assertWriteThenReadGivesInitialValue(initialValue, new TypeToken<ImportId>(){});
    }
    
}

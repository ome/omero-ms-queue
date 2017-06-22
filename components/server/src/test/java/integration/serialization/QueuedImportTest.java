package integration.serialization;

import ome.smuggler.core.types.ImportBatchId;
import ome.smuggler.core.types.ImportId;
import ome.smuggler.core.types.QueuedImport;

import org.junit.Test;

import com.google.gson.reflect.TypeToken;


public class QueuedImportTest extends JsonWriteReadTest {

    @Test
    @SuppressWarnings("unchecked")
    public void serializeAndDeserialize() {
        QueuedImport initialValue = 
                new QueuedImport(new ImportId(new ImportBatchId()),
                                 ImportInputTest.makeNew());
        Class<QueuedImport> valueType = (Class<QueuedImport>) 
                                        initialValue.getClass();
        TypeToken<QueuedImport> typeToken = new TypeToken<QueuedImport>(){}; 
        
        assertWriteThenReadGivesInitialValue(initialValue, valueType);
        assertWriteThenReadGivesInitialValue(initialValue, typeToken);
    }
    
}

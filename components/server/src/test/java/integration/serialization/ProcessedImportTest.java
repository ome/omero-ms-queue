package integration.serialization;

import com.google.gson.reflect.TypeToken;
import ome.smuggler.core.types.ImportBatchId;
import ome.smuggler.core.types.ImportId;
import ome.smuggler.core.types.ProcessedImport;
import ome.smuggler.core.types.QueuedImport;
import org.junit.Test;


public class ProcessedImportTest extends JsonWriteReadTest {

    @Test
    @SuppressWarnings("unchecked")
    public void serializeAndDeserialize() {
        QueuedImport task =
                new QueuedImport(new ImportId(new ImportBatchId()),
                        ImportInputTest.makeNew());
        ProcessedImport initialValue = ProcessedImport.succeeded(task);
        Class<ProcessedImport> valueType = (Class<ProcessedImport>)
                initialValue.getClass();
        TypeToken<ProcessedImport> typeToken = new TypeToken<ProcessedImport>(){};

        assertWriteThenReadGivesInitialValue(initialValue, valueType);
        assertWriteThenReadGivesInitialValue(initialValue, typeToken);
    }

}

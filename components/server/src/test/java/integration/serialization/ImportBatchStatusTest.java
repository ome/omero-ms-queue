package integration.serialization;

import static ome.smuggler.core.types.ProcessedImport.*;

import java.util.stream.Stream;
import com.google.gson.reflect.TypeToken;

import ome.smuggler.core.types.ImportBatch;
import ome.smuggler.core.types.ImportBatchStatus;
import ome.smuggler.core.types.ImportInput;

import org.junit.Test;


public class ImportBatchStatusTest extends JsonWriteReadTest {

    @Test
    @SuppressWarnings("unchecked")
    public void serializeAndDeserialize() {
        ImportInput in1 = ImportInputTest.makeNew(),
                    in2 = ImportInputTest.makeNew();
        ImportBatch batch = new ImportBatch(Stream.of(in1, in2));
        ImportBatchStatus initialValue = new ImportBatchStatus(batch);
        initialValue.addToCompleted(
                succeeded(batch.imports().findFirst().get()));
        initialValue.addToCompleted(
                failed(batch.imports().skip(1).findFirst().get()));

        Class<ImportBatchStatus> valueType = (Class<ImportBatchStatus>)
                initialValue.getClass();
        TypeToken<ImportBatchStatus> typeToken =
                new TypeToken<ImportBatchStatus>(){};

        assertWriteThenReadGivesInitialValue(initialValue, valueType);
        assertWriteThenReadGivesInitialValue(initialValue, typeToken);
    }

}

package integration.serialization;

import java.util.stream.Stream;
import com.google.gson.reflect.TypeToken;

import ome.smuggler.core.types.ImportBatch;
import ome.smuggler.core.types.ImportInput;

import org.junit.Test;


public class ImportBatchTest extends JsonWriteReadTest {

    @Test
    @SuppressWarnings("unchecked")
    public void serializeAndDeserialize() {
        Stream<ImportInput> imports = Stream.of(
                ImportInputTest.makeNew(), ImportInputTest.makeNew());
        ImportBatch initialValue = new ImportBatch(imports);
        Class<ImportBatch> valueType = (Class<ImportBatch>)
                initialValue.getClass();
        TypeToken<ImportBatch> typeToken = new TypeToken<ImportBatch>(){};

        assertWriteThenReadGivesInitialValue(initialValue, valueType);
        assertWriteThenReadGivesInitialValue(initialValue, typeToken);
    }

}

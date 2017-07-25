package ome.smuggler.core.service.imports.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static ome.smuggler.core.service.imports.impl.Utils.*;

import kew.core.msg.RepeatAction;
import ome.smuggler.core.types.ImportBatch;
import ome.smuggler.core.types.ImportBatchStatus;
import ome.smuggler.core.types.ProcessedImport;
import org.junit.Before;
import org.junit.Test;


public class BatchUpdateHandlerTest {

    private BatchUpdateHandler target;
    private ImportEnv env;

    @Before
    public void setup() {
        env = mockedImportEnvWithMemBatchStore();
        target = new BatchUpdateHandler(env);
    }

    @Test
    public void repeatOnException() {
        ProcessedImport processed = succeededProcessedImport();
        try {
            env.batchManager().updateBatchOf(processed);
        } catch (IllegalArgumentException e) {
            // batch key not in batch store ==> exc when trying to access value.
            RepeatAction actual = target.consume(processed);
            assertThat(actual, is(RepeatAction.Repeat));
        }
    }

    @Test
    public void stopIfNoExceptions() {
        ImportBatch batch = env.batchManager()
                               .createBatchFor(newImportRequests(2));
        ProcessedImport processed = ProcessedImport.succeeded(
                batch.imports().findFirst().get());

        RepeatAction actual = target.consume(processed);
        assertThat(actual, is(RepeatAction.Stop));

        ImportBatchStatus status = batchStoreData(env).get(processed.batchId());
        assertTrue(status.succeeded().contains(processed.queued()));
    }

    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfNullEnv() {
        new BatchUpdateHandler(null);
    }

    @Test(expected = NullPointerException.class)
    public void consumeThrowsIfNullProcessedImport() {
        target.consume(null);
    }

}

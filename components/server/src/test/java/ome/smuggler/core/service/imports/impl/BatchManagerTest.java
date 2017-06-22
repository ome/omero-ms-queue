package ome.smuggler.core.service.imports.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static ome.smuggler.core.service.imports.impl.Utils.*;

import ome.smuggler.core.msg.ChannelMessage;
import ome.smuggler.core.types.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.function.Function;

public class BatchManagerTest {

    private static ProcessedImport processAt(
            int ix, ImportBatch batch,
            Function<QueuedImport, ProcessedImport> f) {
        return batch.imports().map(f).skip(ix).findFirst().get();
    }

    private static ProcessedImport succeedAt(int ix, ImportBatch batch) {
        return processAt(ix, batch, ProcessedImport::succeeded);
    }

    private static ProcessedImport failAt(int ix, ImportBatch batch) {
        return processAt(ix, batch, ProcessedImport::failed);
    }


    private BatchManager target;
    private ImportEnv env;

    @Before
    public void setup() {
        env = mockedImportEnvWithMemBatchStore();
        target = new BatchManager(env);
    }

    @Test
    public void createInitialUnprocessedStatus() {
        ImportBatch batch = target.createBatchFor(newImportRequests(2));
        assertNotNull(batch);

        ProcessedImport first = succeedAt(0, batch);
        ImportBatchStatus actual = target.getBatchStatusOf(first);
        ImportBatchStatus expected = new ImportBatchStatus(batch);
        assertThat(actual, is(expected));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getBatchStatusOfThrowsNoBatch() {
        assertThat(batchStoreData(env).size(), is(0));
        target.getBatchStatusOf(succeededProcessedImport());
    }

    @Test
    public void deleteBatchDoesNothingIfNoBatch() {
        assertThat(batchStoreData(env).size(), is(0));
        target.deleteBatch(succeededProcessedImport().batchId());
        assertThat(batchStoreData(env).size(), is(0));
    }

    @Test
    public void deleteExistingBatch() {
        ImportBatch batch = target.createBatchFor(newImportRequests(1));
        assertThat(batchStoreData(env).size(), is(1));

        target.deleteBatch(batch.batchId());
        assertThat(batchStoreData(env).size(), is(0));
    }

    @Test
    public void updateWithFailedImport() {
        ImportBatch batch = target.createBatchFor(newImportRequests(2));
        ProcessedImport failed = failAt(0, batch);
        target.updateBatchOf(failed);
        ImportBatchStatus status = target.getBatchStatusOf(failed);

        assertFalse(status.allProcessed());
        assertTrue(status.failed().contains(failed.queued()));
        assertThat(status.succeeded().size(), is(0));
    }

    @Test
    public void updateWithSucceededImport() {
        ImportBatch batch = target.createBatchFor(newImportRequests(2));
        ProcessedImport succeeded = succeedAt(1, batch);
        target.updateBatchOf(succeeded);
        ImportBatchStatus status = target.getBatchStatusOf(succeeded);

        assertFalse(status.allProcessed());
        assertTrue(status.succeeded().contains(succeeded.queued()));
        assertThat(status.failed().size(), is(0));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void updateRaisesBatchCompletedEventWhenBatchDone() {
        ImportBatch batch = target.createBatchFor(newImportRequests(2));
        target.updateBatchOf(failAt(0, batch));
        target.updateBatchOf(succeedAt(1, batch));
        ImportBatchStatus status = target.getBatchStatusOf(failAt(0, batch));

        assertTrue(status.allProcessed());

        ArgumentCaptor<ChannelMessage> message =
                // ChannelMessage<FutureTimepoint, ProcessedImport>
                ArgumentCaptor.forClass(ChannelMessage.class);
        verify(env.gcQueue()).uncheckedSend(message.capture());
        ProcessedImport actual = (ProcessedImport) message.getValue().data();

        assertThat(actual.status(), is(ImportFinalisationPhase.BatchCompleted));
    }

    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfNullEnv() {
        new BatchManager(null);
    }

    @Test(expected = NullPointerException.class)
    public void createBatchForThrowsIfNullEnv() {
        target.createBatchFor(null);
    }

    @Test(expected = NullPointerException.class)
    public void getBatchStatusOfThrowsIfNullProcessedImport() {
        target.getBatchStatusOf(null);
    }

    @Test(expected = NullPointerException.class)
    public void deleteBatchThrowsIfNullImportBatchId() {
        target.deleteBatch(null);
    }

    @Test(expected = NullPointerException.class)
    public void updateBatchOfThrowsIfNullProcessedImport() {
        target.updateBatchOf(null);
    }

}

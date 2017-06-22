package ome.smuggler.core.service.imports.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static ome.smuggler.core.service.imports.impl.Utils.*;

import ome.smuggler.core.msg.RepeatAction;
import ome.smuggler.core.msg.SchedulingSource;
import ome.smuggler.core.types.*;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;


public class BatchCompletedHandlerTest {

    private BatchCompletedHandler target;
    private ImportEnv env;
    private SchedulingSource<ProcessedImport> gcQueueMock;
    private ImportConfigSource configMock;

    @Before
    public void setup() {
        env = mockedImportEnvWithMemBatchStore();
        target = new BatchCompletedHandler(env);
        gcQueueMock = env.gcQueue();
        configMock = env.config();
    }

    @Test
    public void repeatOnExceptionWhenAccessingBatchStore() {
        ProcessedImport processed = succeededProcessedImport();
        // batch key not in batch store ==> exc when trying to access value.

        RepeatAction actual = target.consume(processed);
        assertThat(actual, is(RepeatAction.Repeat));
        verify(gcQueueMock, never()).uncheckedSend(any());
    }

    @Test
    public void repeatOnExceptionWhenSchedulingDeletion() {
        ImportBatch batch = env.batchManager()
                               .createBatchFor(newImportRequests(1));
        ProcessedImport processed = ProcessedImport.succeeded(
                                        batch.imports().findFirst().get());

        when(configMock.logRetentionPeriod()).thenReturn(Duration.ZERO);
        doThrow(new RuntimeException()).when(gcQueueMock).uncheckedSend(any());

        RepeatAction actual = target.consume(processed);
        assertThat(actual, is(RepeatAction.Repeat));
        verify(env.gcQueue(), times(1)).uncheckedSend(any());
    }

    @Test
    public void stopIfNoExceptions() {
        ImportBatch batch = env.batchManager()
                               .createBatchFor(newImportRequests(1));
        ProcessedImport processed = ProcessedImport.succeeded(
                                        batch.imports().findFirst().get());
        when(configMock.logRetentionPeriod()).thenReturn(Duration.ZERO);

        RepeatAction actual = target.consume(processed);
        assertThat(actual, is(RepeatAction.Stop));
    }

    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfNullEnv() {
        new BatchCompletedHandler(null);
    }

    @Test(expected = NullPointerException.class)
    public void consumeThrowsIfNullProcessedImport() {
        target.consume(null);
    }
}

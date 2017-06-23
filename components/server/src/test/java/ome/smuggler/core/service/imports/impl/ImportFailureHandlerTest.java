package ome.smuggler.core.service.imports.impl;

import ome.smuggler.core.msg.ChannelMessage;
import ome.smuggler.core.types.ImportId;
import ome.smuggler.core.types.ProcessedImport;
import ome.smuggler.core.types.QueuedImport;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.nio.file.Path;
import java.nio.file.Paths;

import static ome.smuggler.core.service.imports.impl.Utils.*;
import static ome.smuggler.core.types.ImportFinalisationPhase.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ImportFailureHandlerTest {

    private ImportFailureHandler target;
    private ImportEnv env;

    @Before
    public void setup() {
        env = mockedImportEnvWithMemBatchStore();
        target = new ImportFailureHandler(env);

        when(env.config().importLogDir())
                .thenReturn(Paths.get("non-existing/but/irrelevant"));
    }

    @SuppressWarnings("unchecked")
    private void assertUpdateMessageFor(QueuedImport task) {
        ArgumentCaptor<ChannelMessage> message =
                // ChannelMessage<FutureTimepoint, ProcessedImport>
                ArgumentCaptor.forClass(ChannelMessage.class);
        verify(env.gcQueue(), times(1)).uncheckedSend(message.capture());

        ProcessedImport actual = (ProcessedImport) message.getValue().data();
        assertFalse(actual.succeeded());
        assertThat(actual.queued(), is(task));
        assertThat(actual.status(), is(BatchStillInProgress));
    }

    @Test
    public void raiseBatchUpdateEventEvenIfExceptionWhileHandling() {
        RuntimeException re = new RuntimeException(
                "fail when adding import log to failed store");
        QueuedImport task = newQueuedImport();
        ImportId taskId = task.getTaskId();
        Path importLog = env.importLogPathFor(taskId).get();

        doThrow(re).when(env.failedImportLogStore()).add(taskId, importLog);
        try {
            target.accept(task);
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is(re.getMessage()));
            assertUpdateMessageFor(task);
        }
    }

    @Test
    public void raiseBatchUpdateEventWhenFinishedHandlingWithNoExceptions() {
        QueuedImport task = newQueuedImport();
        target.accept(task);
        assertUpdateMessageFor(task);
    }

    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfNullEnv() {
        new ImportFailureHandler(null);
    }

    @Test(expected = NullPointerException.class)
    public void acceptThrowsIfNullQueuedImport() {
        target.accept(null);
    }

}

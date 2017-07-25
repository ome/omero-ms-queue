package ome.smuggler.core.service.imports.impl;

import kew.core.msg.ChannelMessage;
import kew.core.msg.RepeatAction;
import ome.smuggler.core.types.ProcessedImport;
import ome.smuggler.core.types.QueuedImport;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;

import static ome.smuggler.core.service.imports.impl.Utils.*;
import static ome.smuggler.core.types.ImportFinalisationPhase.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ImportRunnerTest {

    private ImportRunner target;
    private ImportEnv env;

    @Rule
    public final TemporaryFolder logDir = new TemporaryFolder();

    @Before
    public void setup() {
        env = mockedImportEnvWithMemBatchStore();
        target = new ImportRunner(env);

        when(env.config().importLogDir())
                .thenReturn(logDir.getRoot().toPath());
    }

    @SuppressWarnings("unchecked")
    private void assertUpdateMessageFor(QueuedImport task) {
        ArgumentCaptor<ChannelMessage> message =
                // ChannelMessage<FutureTimepoint, ProcessedImport>
                ArgumentCaptor.forClass(ChannelMessage.class);
        verify(env.gcQueue(), times(1)).uncheckedSend(message.capture());

        ProcessedImport actual = (ProcessedImport) message.getValue().data();
        assertTrue(actual.succeeded());
        assertThat(actual.queued(), is(task));
        assertThat(actual.status(), is(BatchStillInProgress));
    }

    @Test
    public void raiseBatchUpdateEventAndStopWhenSuccessful() {
        QueuedImport task = newQueuedImport();
        when(env.importer().run(any(), any())).thenReturn(true);

        RepeatAction actual = target.consume(task);

        assertThat(actual, is(RepeatAction.Stop));
        assertUpdateMessageFor(task);
    }

    @Test
    public void dontRaiseBatchUpdateEventAndRepeatWhenNotSuccessful() {
        QueuedImport task = newQueuedImport();
        when(env.importer().run(any(), any())).thenReturn(false);

        RepeatAction actual = target.consume(task);

        assertThat(actual, is(RepeatAction.Repeat));
        verify(env.gcQueue(), times(0)).uncheckedSend(any());
    }

    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfNullEnv() {
        new ImportRunner(null);
    }

    @Test(expected = NullPointerException.class)
    public void consumeThrowsIfNullQueuedImport() {
        target.consume(null);
    }

}

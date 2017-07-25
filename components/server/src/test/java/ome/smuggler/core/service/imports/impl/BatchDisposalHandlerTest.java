package ome.smuggler.core.service.imports.impl;

import kew.core.msg.RepeatAction;
import ome.smuggler.core.types.ImportBatch;
import ome.smuggler.core.types.ImportConfigSource;
import ome.smuggler.core.types.ProcessedImport;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static ome.smuggler.core.service.imports.impl.Utils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BatchDisposalHandlerTest {

    private BatchDisposalHandler target;
    private ImportEnv env;
    private ImportConfigSource configMock;

    @Rule
    public final TemporaryFolder logDir = new TemporaryFolder();

    @Before
    public void setup() {
        env = mockedImportEnvWithMemBatchStore();
        configMock = env.config();
        target = new BatchDisposalHandler(env);
    }

    @Test
    public void repeatOnExceptionWhenAccessingBatchStore() {
        ProcessedImport processed = succeededProcessedImport();
        // batch key not in batch store ==> exc when trying to access value.

        RepeatAction actual = target.consume(processed);
        assertThat(actual, is(RepeatAction.Repeat));
    }

    @Test
    public void repeatOnExceptionWhenDeletingImportLog() {
        ImportBatch batch = env.batchManager()
                .createBatchFor(newImportRequests(1));
        ProcessedImport processed = ProcessedImport.succeeded(
                batch.imports().findFirst().get());
        when(configMock.importLogDir())
                .thenThrow(new RuntimeException("log dir"));

        RepeatAction actual = target.consume(processed);
        assertThat(actual, is(RepeatAction.Repeat));
    }

    @Test
    public void stopIfNoExceptions() {
        ImportBatch batch = env.batchManager()
                .createBatchFor(newImportRequests(1));
        ProcessedImport processed = ProcessedImport.succeeded(
                batch.imports().findFirst().get());
        when(configMock.importLogDir())
                .thenReturn(logDir.getRoot().toPath());

        RepeatAction actual = target.consume(processed);
        assertThat(actual, is(RepeatAction.Stop));
    }

    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfNullEnv() {
        new BatchDisposalHandler(null);
    }

    @Test(expected = NullPointerException.class)
    public void consumeThrowsIfNullProcessedImport() {
        target.consume(null);
    }

}

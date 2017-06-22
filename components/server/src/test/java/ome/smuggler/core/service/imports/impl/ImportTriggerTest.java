package ome.smuggler.core.service.imports.impl;

import ome.smuggler.core.msg.ChannelMessage;
import ome.smuggler.core.types.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ome.smuggler.core.service.imports.impl.Utils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ImportTriggerTest {

    private ImportTrigger target;
    private ImportEnv env;

    @Rule
    public TemporaryFolder logDir = new TemporaryFolder();

    @Before
    public void setup() {
        env = mockedImportEnvWithMemBatchStore();
        target = new ImportTrigger(env);

        when(env.config().importLogDir())
                .thenReturn(logDir.getRoot().toPath());
    }

    @Test
    public void createNewBatchOnEachCall() {
        assertThat(batchStoreData(env).size(), is(0));
        ImportBatch batch = target.enqueue(newImportRequests(2));

        assertThat(batchStoreData(env).size(), is(1));
        ImportBatch stored = batchStoreData(env).get(batch.batchId()).batch();
        assertThat(batch, is(stored));
    }

    @Test
    public void enqueueOneImport() {
        ImportInput request = newImportInput("k");
        ArgumentCaptor<QueuedImport> message =
                ArgumentCaptor.forClass(QueuedImport.class);

        target.enqueue(request);

        verify(env.queue(), times(1)).uncheckedSend(message.capture());
        ImportInput actual = message.getValue().getRequest();
        assertThat(actual, is(request));
    }

    @Test
    public void enqueueManyImports() {
        Set<ImportInput> requests = newImportRequests(2)
                                   .collect(Collectors.toSet());
        ArgumentCaptor<QueuedImport> message =
                ArgumentCaptor.forClass(QueuedImport.class);

        target.enqueue(requests.stream());

        verify(env.queue(), times(2)).uncheckedSend(message.capture());
        Set<ImportInput> actuals = message.getAllValues()
                                          .stream()
                                          .map(QueuedImport::getRequest)
                                          .collect(Collectors.toSet());
        assertThat(actuals, is(requests));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void raiseBatchUpdateEventsOnException() {
        Set<ImportInput> requests = newImportRequests(2)
                                   .collect(Collectors.toSet());
        ArgumentCaptor<ChannelMessage> message =
                // ChannelMessage<FutureTimepoint, ProcessedImport>
                ArgumentCaptor.forClass(ChannelMessage.class);
        doThrow(new RuntimeException()).when(env.queue()).uncheckedSend(any());

        target.enqueue(requests.stream());

        verify(env.gcQueue(), times(2)).uncheckedSend(message.capture());
        Set<ImportInput> actuals = message.getAllValues()
                                          .stream()
                                          .map(x -> (ProcessedImport) x.data())
                                          .map(ProcessedImport::queued)
                                          .map(QueuedImport::getRequest)
                                          .collect(Collectors.toSet());
        assertThat(actuals, is(requests));
    }

    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfNullEnv() {
        new ImportTrigger(null);
    }

    @Test(expected = NullPointerException.class)
    public void enqueueThrowsIfNullRequests() {
        target.enqueue((Stream<ImportInput>) null);
    }

    @Test(expected = NullPointerException.class)
    public void enqueueThrowsIfSomeRequestsAreNull() {
        target.enqueue(Stream.of(newImportInput("k"), null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void enqueueThrowsIfEmptyRequests() {
        target.enqueue(Stream.empty());
    }

}

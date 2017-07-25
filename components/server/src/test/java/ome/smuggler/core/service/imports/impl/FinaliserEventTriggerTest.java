package ome.smuggler.core.service.imports.impl;

import kew.core.msg.ChannelMessage;
import ome.smuggler.core.types.ProcessedImport;
import ome.smuggler.core.types.QueuedImport;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.function.Consumer;

import static ome.smuggler.core.service.imports.impl.Utils.*;
import static ome.smuggler.core.types.ImportFinalisationPhase.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;


public class FinaliserEventTriggerTest {

    public static Finaliser dummyFinaliser() {
        return new Finaliser(dummyImportEnv());
    }

    @SuppressWarnings("unchecked")
    private void raiseAsyncEventTestRunner(Consumer<Finaliser> trigger,
                                           Consumer<ProcessedImport> asserts) {
        ImportEnv mockEnv = fullyMockedImportEnv();
        ArgumentCaptor<ChannelMessage> message =
                // ChannelMessage<FutureTimepoint, ProcessedImport>
                ArgumentCaptor.forClass(ChannelMessage.class);
        Finaliser target = new Finaliser(mockEnv);

        trigger.accept(target);

        verify(mockEnv.gcQueue()).uncheckedSend(message.capture());
        ProcessedImport actual = (ProcessedImport) message.getValue().data();

        asserts.accept(actual);
    }

    @Test
    public void onFailureRaisesAsyncEvent() {
        QueuedImport processed = newQueuedImport();
        raiseAsyncEventTestRunner(
            finaliser -> finaliser.onFailure(processed),
            actual -> {
                assertFalse(actual.succeeded());
                assertThat(actual.queued(), is(processed));
                assertThat(actual.status(), is(BatchStillInProgress));
            });
    }

    @Test
    public void onSuccessRaisesAsyncEvent() {
        QueuedImport processed = newQueuedImport();
        raiseAsyncEventTestRunner(
                finaliser -> finaliser.onSuccess(processed),
                actual -> {
                    assertTrue(actual.succeeded());
                    assertThat(actual.queued(), is(processed));
                    assertThat(actual.status(), is(BatchStillInProgress));
                });
    }

    private void assertOnBatchCompletionRaisesAsyncEvent(boolean success) {
        ProcessedImport processed = success ? succeededProcessedImport()
                                            : failedProcessedImport();
        raiseAsyncEventTestRunner(
                finaliser -> finaliser.onBatchCompletion(processed),
                actual -> assertThat(actual.status(), is(BatchCompleted)));
    }

    @Test
    public void onBatchCompletionRaisesAsyncEvent() {
        assertOnBatchCompletionRaisesAsyncEvent(true);
        assertOnBatchCompletionRaisesAsyncEvent(false);
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullEnv() {
        new Finaliser(null);
    }

    @Test(expected = NullPointerException.class)
    public void onSuccessThrowsIfNullQueuedImport() {
        dummyFinaliser().onSuccess(null);
    }

    @Test(expected = NullPointerException.class)
    public void onFailureThrowsIfNullQueuedImport() {
        dummyFinaliser().onFailure(null);
    }

    @Test(expected = NullPointerException.class)
    public void onBatchCompletionThrowsIfNullProcessedImport() {
        dummyFinaliser().onBatchCompletion(null);
    }

}

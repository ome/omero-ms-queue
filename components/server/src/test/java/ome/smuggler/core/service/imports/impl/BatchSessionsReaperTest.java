package ome.smuggler.core.service.imports.impl;

import ome.smuggler.core.service.omero.SessionService;
import ome.smuggler.core.types.ImportBatch;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static ome.smuggler.core.service.imports.impl.Utils.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class BatchSessionsReaperTest {

    private BatchSessionsReaper target;
    private ImportEnv env;
    private SessionService sessionService;

    @Before
    public void setup() {
        env = mockedImportEnvWithMemBatchStore();
        sessionService = env.session();
        target = new BatchSessionsReaper(sessionService);
    }

    @Test
    public void batchWithOneSession() {
        ImportBatch batch = env.batchManager()
                               .createBatchFor(newImportRequests(1));
        Optional<Throwable>[] maybeE = target.closeSessions(batch);

        assertNotNull(maybeE);
        assertThat(maybeE.length, is(1));
        assertThat(maybeE[0], is(Optional.empty()));
    }

    @Test
    public void batchWithOneSessionThatFailsToClose() {
        RuntimeException errorOnClose = new RuntimeException("error on close");
        when(sessionService.close(any(), any())).thenThrow(errorOnClose);
        ImportBatch batch = env.batchManager()
                               .createBatchFor(newImportRequests(1));
        Optional<Throwable>[] maybeE = target.closeSessions(batch);

        assertNotNull(maybeE);
        assertThat(maybeE.length, is(1));
        assertTrue(maybeE[0].isPresent());
        assertThat(maybeE[0].get(), is(errorOnClose));
    }

    @Test
    public void batchWithSomeSessionsThatFailToClose() {
        Throwable e0 = new RuntimeException("e0"),
                  e2 = new RuntimeException("e2");
        ImportBatch batch = env.batchManager()
                           .createBatchFor(newImportRequests("s0", "s1", "s2"));

        URI omero = batch.imports().findFirst().get().getRequest().getOmeroHost();
        when(sessionService.close(omero, "s0")).thenThrow(e0);
        when(sessionService.close(omero, "s1")).thenReturn(true);
        when(sessionService.close(omero, "s2")).thenThrow(e2);

        Optional<Throwable>[] maybeE = target.closeSessions(batch);
        Set<Optional<Throwable>> actual = new HashSet<>(Arrays.asList(maybeE));
        Set<Optional<Throwable>> expected = Stream.of(e0, null, e2)
                                                  .map(Optional::ofNullable)
                                                  .collect(toSet());

        assertThat(actual, is(expected));
    }

    @Test
    public void batchWithAllImportsSharingSameSession() {
        ImportBatch batch = env.batchManager()
                               .createBatchFor(newImportRequests(3));
        Optional<Throwable>[] maybeE = target.closeSessions(batch);

        assertNotNull(maybeE);
        assertThat(maybeE.length, is(1));
        assertFalse(maybeE[0].isPresent());
        verify(sessionService).close(any(), any());
    }

    @Test
    public void batchWithSomeImportsSharingSameSession() {
        ImportBatch batch = env.batchManager()
                           .createBatchFor(newImportRequests("s", "k", "s"));
        Optional<Throwable>[] maybeE = target.closeSessions(batch);

        assertNotNull(maybeE);
        assertThat(maybeE.length, is(2));
        assertFalse(maybeE[0].isPresent());
        assertFalse(maybeE[1].isPresent());

        URI omero = batch.imports().findFirst().get().getRequest().getOmeroHost();
        verify(sessionService).close(omero, "s");
        verify(sessionService).close(omero, "k");
    }

    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfNullEnv() {
        new BatchSessionsReaper(null);
    }

    @Test(expected = NullPointerException.class)
    public void closeSessionsThrowsIfNullImportBatch() {
        target.closeSessions(null);
    }

}

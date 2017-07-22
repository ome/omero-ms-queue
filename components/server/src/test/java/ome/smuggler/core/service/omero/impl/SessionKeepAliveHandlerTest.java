package ome.smuggler.core.service.omero.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.time.Duration;
import java.util.Optional;

import ome.smuggler.config.items.OmeCliConfig;
import ome.smuggler.core.msg.ChannelSource;
import ome.smuggler.core.msg.CountedSchedule;
import ome.smuggler.core.service.file.RemotePathResolver;
import ome.smuggler.core.service.omero.SessionService;
import util.types.FutureTimepoint;
import ome.smuggler.core.types.OmeCliConfigReader;
import ome.smuggler.core.types.QueuedOmeroKeepAlive;
import util.types.Schedule;
import org.junit.Test;


public class SessionKeepAliveHandlerTest {

    interface SessionQ extends ChannelSource<QueuedOmeroKeepAlive> {}


    private static OmeroEnv mockEnv() {
        OmeCliConfig cfg = new OmeCliConfig();
        cfg.setOmeCliJarPath("no-where");
        OmeCliConfigReader reader = new OmeCliConfigReader(cfg);

        return new OmeroEnv(reader,
                            mock(SessionQ.class),
                            mock(RemotePathResolver.class),
                            mock(OmeroLogger.class));
    }

    private static SessionService mockService(boolean successOrFailure) {
        SessionService service = mock(SessionService.class);
        when(service.keepAlive(any(), any())).thenReturn(successOrFailure);

        return service;
    }

    private static SessionKeepAliveHandler newHandler(
            boolean serviceSuccessOrFailure) {
        return new SessionKeepAliveHandler(
                mockEnv(), mockService(serviceSuccessOrFailure));
    }

    private static QueuedOmeroKeepAlive message(Duration inHowLongToStop) {
        FutureTimepoint stopTime = new FutureTimepoint(inHowLongToStop);
        return new QueuedOmeroKeepAlive(URI.create("h:1"), "sesh", stopTime);
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullEnv() {
        new SessionKeepAliveHandler(null, mockService(true));
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullService() {
        new SessionKeepAliveHandler(mockEnv(), null);
    }

    @Test
    public void repeatOnSuccessAsLongAsNotReachedEndTimepoint() {
        SessionKeepAliveHandler target = newHandler(true);
        QueuedOmeroKeepAlive stopInTheFuture = message(Duration.ofMinutes(1));
        Optional<Schedule<QueuedOmeroKeepAlive>> actual =
                target.consume(CountedSchedule.first(), stopInTheFuture);

        assertNotNull(actual);
        assertTrue(actual.isPresent());
        assertThat(actual.get().what(), is(stopInTheFuture));
    }

    @Test
    public void stopOnSuccessWhenReachedEndTimepoint() {
        SessionKeepAliveHandler target = newHandler(true);
        QueuedOmeroKeepAlive stopNow = message(Duration.ZERO);
        Optional<Schedule<QueuedOmeroKeepAlive>> actual =
                target.consume(CountedSchedule.first(), stopNow);

        assertNotNull(actual);
        assertFalse(actual.isPresent());
    }

    @Test
    public void stopOnFailureEvenIfEndTimepointNotReached() {
        SessionKeepAliveHandler target = newHandler(false);
        QueuedOmeroKeepAlive stopInTheFuture = message(Duration.ofMinutes(1));
        Optional<Schedule<QueuedOmeroKeepAlive>> actual =
                target.consume(CountedSchedule.first(), stopInTheFuture);

        assertNotNull(actual);
        assertFalse(actual.isPresent());
    }

    @Test
    public void stopOnFailureWhenReachedEndTimepoint() {
        SessionKeepAliveHandler target = newHandler(false);
        QueuedOmeroKeepAlive stopNow = message(Duration.ZERO);
        Optional<Schedule<QueuedOmeroKeepAlive>> actual =
                target.consume(CountedSchedule.first(), stopNow);

        assertNotNull(actual);
        assertFalse(actual.isPresent());
    }

}

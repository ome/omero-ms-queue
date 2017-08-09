package ome.smuggler.core.types;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.types.FutureTimepoint.now;

import org.junit.Test;
import util.types.FutureTimepoint;

import java.net.URI;

public class QueuedOmeroKeepAliveTest {

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullOmero() {
        new QueuedOmeroKeepAlive(null, "sesh", now());
    }

    @Test (expected = IllegalArgumentException.class)
    public void ctorThrowsIfNullSessionKey() {
        new QueuedOmeroKeepAlive(URI.create("h:1"), null, now());
    }

    @Test (expected = IllegalArgumentException.class)
    public void ctorThrowsIfEmptySessionKey() {
        new QueuedOmeroKeepAlive(URI.create("h:1"), "", now());
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullTimepoint() {
        new QueuedOmeroKeepAlive(URI.create("h:1"), "sesh", null);
    }

    @Test
    public void enforceValueEquality() {
        URI omero = URI.create("h:1");
        String sessionKey = "sk";
        FutureTimepoint now = now();
        QueuedOmeroKeepAlive value =
                new QueuedOmeroKeepAlive(omero, sessionKey, now);
        QueuedOmeroKeepAlive valueCopy =
                new QueuedOmeroKeepAlive(omero, sessionKey, now);

        assertThat(value.getOmero(), is(omero));
        assertThat(value.getSessionKey(), is(sessionKey));
        assertThat(value.getUntilWhen(), is(now));

        assertThat(value, is(valueCopy));
        assertThat(value.hashCode(), is(valueCopy.hashCode()));
    }

}

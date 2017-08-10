package kew.core.msg;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class DisconnectableTest {

    static class Resource implements Disconnectable {

        static boolean hasCalledClose;
        static IllegalArgumentException throwFromClose;

        void shouldThrow(boolean yes) {
            throwFromClose = yes ? new IllegalArgumentException() : null;
        }

        @Override
        public void close() throws Exception {
            hasCalledClose = true;
            if (throwFromClose != null) throw throwFromClose;
        }
    }

    @Before
    public void setup() {
        Resource.hasCalledClose = false;
        Resource.throwFromClose = null;
    }

    @Test
    public void autoCloseWithoutException() throws Exception {
        try (Resource r = new Resource()) {
            r.shouldThrow(false);
        }
        assertTrue(Resource.hasCalledClose);
    }

    @Test
    public void autoCloseWithException() throws Exception {
        try {
            try (Resource r = new Resource()) {
                r.shouldThrow(true);
            }
            fail("close should've thrown!");
        } catch (IllegalArgumentException e) {
            assertThat(Resource.throwFromClose, is(e));
        }
        assertTrue(Resource.hasCalledClose);
    }

    @Test
    public void disconnectCallsClose() {
        Resource r = new Resource();
        r.disconnect();

        assertTrue(Resource.hasCalledClose);
    }

}

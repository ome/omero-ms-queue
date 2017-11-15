package kew.providers.artemis.qchan;

import static org.mockito.Mockito.*;

import kew.providers.artemis.ServerConnector;
import org.apache.activemq.artemis.core.config.CoreQueueConfiguration;
import org.junit.Test;


public class ArtemisQChannelTest {

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullConnector() {
        new ArtemisQChannel<>(null,
                              new CoreQueueConfiguration(),
                              (out, t) -> {},
                              in -> null);
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullQConfig() throws Exception {
        new ArtemisQChannel<>(mock(ServerConnector.class),
                              new CoreQueueConfiguration(),
                              (out, t) -> {},
                              in -> null);
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullSerializer() throws Exception {
        new ArtemisQChannel<>(mock(ServerConnector.class),
                             new CoreQueueConfiguration(),
                             null,
                             in -> null);
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullDeserializer() throws Exception {
        new ArtemisQChannel<>(mock(ServerConnector.class),
                              new CoreQueueConfiguration(),
                              (out, t) -> {},
                              null);
    }

}

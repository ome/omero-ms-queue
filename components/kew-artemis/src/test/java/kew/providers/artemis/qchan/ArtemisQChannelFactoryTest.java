package kew.providers.artemis.qchan;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import kew.providers.artemis.ServerConnectorTest;
import org.apache.activemq.artemis.core.config.CoreQueueConfiguration;
import org.junit.Test;

public class ArtemisQChannelFactoryTest {

    @Test
    public void hasConnector() throws Exception {
        ArtemisQChannelFactory target = new ArtemisQChannelFactory<>(
                ServerConnectorTest.newConnector(),
                mock(CoreQueueConfiguration.class));

        assertNotNull(target.queue());
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullConnector() {
        new ArtemisQChannelFactory<>(null, mock(CoreQueueConfiguration.class));
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullQConfig() throws Exception {
        new ArtemisQChannelFactory<>(ServerConnectorTest.newConnector(), null);
    }

}

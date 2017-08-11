package kew.providers.artemis;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.api.core.client.ClientSessionFactory;
import org.apache.activemq.artemis.api.core.client.ServerLocator;
import org.junit.Test;

public class ServerConnectorTest {

    static ServerConnector newConnector() throws Exception {
        ServerLocator locator = mock(ServerLocator.class);
        ClientSessionFactory factory = mock(ClientSessionFactory.class);
        ClientSession session = mock(ClientSession.class);

        when(locator.createSessionFactory()).thenReturn(factory);
        when(factory.createSession(anyBoolean(), anyBoolean(), anyInt()))
                .thenReturn(session);

        return new ServerConnector(locator);
    }

    @Test
    public void createSessionOnInstantiation() throws Exception {
        ServerConnector target = newConnector();
        assertNotNull(target.getSession());
    }

    @Test
    public void closeSession() throws Exception {
        ServerConnector target = newConnector();
        target.close();

        ClientSession session = target.getSession();
        verify(session).close();
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullLocator() throws Exception {
        new ServerConnector(null);
    }

}

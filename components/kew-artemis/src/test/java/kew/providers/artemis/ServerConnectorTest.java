package kew.providers.artemis;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.api.core.client.ClientSessionFactory;
import org.apache.activemq.artemis.api.core.client.ServerLocator;
import org.junit.Test;

import java.io.IOException;


public class ServerConnectorTest {

    public static ServerConnector newConnector() throws Exception {
        ServerLocator locator = mock(ServerLocator.class);
        ClientSessionFactory factory = mock(ClientSessionFactory.class);
        ClientSession session = mock(ClientSession.class);

        when(locator.createSessionFactory()).thenReturn(factory);
        doAnswer(invocation -> {
            session.close();
            return null;
        }).when(factory).close();

        return new ServerConnector(locator, csf -> session);
    }

    @Test
    public void createSessionOnInstantiation() throws Exception {
        ServerConnector target = newConnector();
        assertNotNull(target.session());
    }

    @Test
    public void releaseFactoryOnCreateSessionException() throws Exception {
        ServerLocator locator = mock(ServerLocator.class);
        ClientSessionFactory factory = mock(ClientSessionFactory.class);
        when(locator.createSessionFactory()).thenReturn(factory);

        String exceptionMessage = "***";
        try {
            new ServerConnector(locator, csf -> {
                throw new IOException(exceptionMessage);
            });
            fail("should've let the create session exception bubble up!");
        } catch (IOException e) {
            assertThat(e.getMessage(), is(exceptionMessage));
        }
        verify(factory).close();
    }

    @Test
    public void closeSession() throws Exception {
        ServerConnector target = newConnector();
        target.close();

        ClientSession session = target.session();
        verify(session).close();
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullLocator() throws Exception {
        new ServerConnector(null, csf -> mock(ClientSession.class));
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullCreateSessionFactory() throws Exception {
        new ServerConnector(mock(ServerLocator.class), null);
    }

}

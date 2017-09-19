package kew.providers.artemis.runtime;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.api.core.client.ClientSessionFactory;
import org.apache.activemq.artemis.api.core.client.ServerLocator;
import org.junit.Test;

public class ClientSessionsTest {

    private static ClientSessionFactory mockClientSessionFactory()
            throws Exception {
        ServerLocator locator = mock(ServerLocator.class);
        ClientSession session = mock(ClientSession.class);
        ClientSessionFactory csf = mock(ClientSessionFactory.class);
        when(csf.getServerLocator()).thenReturn(locator);
        when(csf.createSession(anyString(), anyString(), anyBoolean(),
                               anyBoolean(), anyBoolean(), anyBoolean(),
                               anyInt()))
                .thenReturn(session);

        return csf;
    }

    @Test
    public void createDefaultSession() throws Exception {
        ClientSessionFactory csf = mockClientSessionFactory();
        ClientSession s = ClientSessions.defaultSession().apply(csf);
        assertNotNull(s);
    }

    @Test
    public void createDefaultAuthenticatedSession() throws Exception {
        ClientSessionFactory csf = mockClientSessionFactory();
        ClientSession s = ClientSessions.defaultAuthenticatedSession(
                                                "user", "pass")
                                        .apply(csf);
        assertNotNull(s);
    }

    @Test
    public void createDefaultAuthenticatedSessionWithNoPass() throws Exception {
        ClientSessionFactory csf = mockClientSessionFactory();
        ClientSession s = ClientSessions.defaultAuthenticatedSession(
                                                "user", null)
                                        .apply(csf);
        assertNotNull(s);
    }

    @Test (expected = NullPointerException.class)
    public void defaultSessionReturnedFunctionThrowsIfNullFactory()
            throws Exception {
        ClientSessions.defaultSession().apply(null);
    }

    @Test (expected = NullPointerException.class)
    public void defaultAuthenticatedSessionReturnedFunctionThrowsIfNullFactory()
            throws Exception {
        ClientSessions.defaultAuthenticatedSession("user", "pass")
                      .apply(null);
    }

    @Test
    public void defaultSessionNeverNull() {
        assertNotNull(ClientSessions.defaultSession());
    }

    @Test
    public void defaultAuthenticatedSessionNeverNull() {
        assertNotNull(
                ClientSessions.defaultAuthenticatedSession("user", "pass"));
    }

    @Test (expected = IllegalArgumentException.class)
    public void defaultAuthenticatedSessionThrowsIfNullUsername() {
        assertNotNull(
                ClientSessions.defaultAuthenticatedSession(null, "pass"));
    }

    @Test (expected = IllegalArgumentException.class)
    public void defaultAuthenticatedSessionThrowsIfEmptyUsername() {
        assertNotNull(
                ClientSessions.defaultAuthenticatedSession("", "pass"));
    }

    @Test
    public void ctor() {
        new ClientSessions();  // only to get 100% coverage.
    }

}

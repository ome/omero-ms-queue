package kew.providers.artemis;

import static java.util.Objects.requireNonNull;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.api.core.client.ClientSessionFactory;
import org.apache.activemq.artemis.api.core.client.ServerLocator;

import util.io.Disconnectable;
import util.lambda.FunctionE;

/**
 * Establishes a connection and client session with the Artemis server.
 */
public class ServerConnector implements Disconnectable {

    private static ClientSession startSession(
            ClientSessionFactory csf,
            FunctionE<ClientSessionFactory, ClientSession> createSession)
                throws ActiveMQException {
        boolean created = false;
        try {
            ClientSession session = createSession.apply(csf);
            session.start();
            created = true;

            return session;
        } finally {
            if (!created) {    // (1)
                csf.close();
            }
        }
    }
    /* NOTES
     * -----
     * 1. Client session factory clean up.
     * We need to close the factory explicitly if we're not using it anymore,
     * otherwise we're leaking resources. In fact, if you don't close it,
     * Artemis will complain loudly about the leak on shut down:
     *
     *   WARN: AMQ212008: I am closing a core ClientSessionFactory you left
     *         open. Please make sure you close all ClientSessionFactories
     *        explicitly before letting them go out of scope!
     */
    
    private final ClientSessionFactory factory;
    private final ClientSession session;
    
    /**
     * Connects to the Artemis server and starts a client session.
     * @param locator locates the Artemis server.
     * @param createSession factory method to create a new session.
     * @throws Exception if the connection could not be established or the
     * session could not be started.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public ServerConnector(
            ServerLocator locator,
            FunctionE<ClientSessionFactory, ClientSession> createSession)
                throws Exception {
        requireNonNull(locator, "locator");
        requireNonNull(createSession, "createSession");
        
        this.factory = locator.createSessionFactory();
        this.session = startSession(factory, createSession);
    }

    /**
     * @return the current Artemis session.
     */
    public ClientSession session() {
        return session;
    }

    /**
     * Closes the current session with the Artemis server.
     * After calling this method any consumers and producers attached to this
     * session won't be usable.
     * @throws Exception if an error occurs.
     */
    @Override
    public void close() throws Exception {
        factory.close();  // closes the session too.
    }

}

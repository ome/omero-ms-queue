package ome.smuggler.providers.q;

import static java.util.Objects.requireNonNull;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.api.core.client.ServerLocator;

/**
 * Establishes a connection and client session with the HornetQ server.
 */
public class ServerConnector {

    private static ClientSession startSession(ClientSessionFactory csf) 
            throws HornetQException {
        ClientSession session = csf.createSession(true, true, 0);  // (*)
        session.start();
        return session;
    }
    /* (*) Removal of messages from the queue.
     * When using the HornetQ core API, consumed messages have to be explicitly 
     * acknowledged for them to be removed from the queue. However, the core API
     * will batch ACK's and send them in one go when the configured batch size
     * is reached. This may cause consumed and acknowledged messages to linger
     * in the queue; by setting the ACK batch size to 0, we ensure messages will
     * be removed as soon as they are acknowledged. 
     * See
     * - http://stackoverflow.com/questions/6452505/hornetq-messages-still-remaining-in-queue-after-consuming-using-core-api
     */
    
    private final ClientSessionFactory factory;
    private final ClientSession session;
    
    /**
     * Connects to the HornetQ server and starts a client session.
     * @param locator locates the HornetQ server.
     * @throws Exception if the connection could not be established or the
     * session could not be started.
     */
    public ServerConnector(ServerLocator locator) throws Exception {
        requireNonNull(locator, "locator");
        
        this.factory = locator.createSessionFactory();
        this.session = startSession(factory);
    }

    /**
     * @return the current HornetQ session.
     */
    public ClientSession getSession() {
        return session;
    }
    
}

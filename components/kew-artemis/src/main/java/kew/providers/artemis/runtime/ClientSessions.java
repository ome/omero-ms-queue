package kew.providers.artemis.runtime;

import static java.util.Objects.requireNonNull;
import static util.string.Strings.requireString;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.api.core.client.ClientSessionFactory;

import util.lambda.FunctionE;

/**
 * Factory methods to create Artemis client sessions.
 */
public class ClientSessions {

    private static ClientSession createSession(ClientSessionFactory csf,
                                               String username,
                                               String password,
                                               boolean xa,
                                               boolean autoCommitSends,
                                               boolean autoCommitAcks,
                                               boolean preAcknowledge,
                                               int ackBatchSize)
            throws ActiveMQException {
        requireNonNull(csf, "csf");
        return csf.createSession(username, password, xa, autoCommitSends,
                autoCommitAcks, preAcknowledge, ackBatchSize);
    }
    // see note (2)

    /**
     * Builds a factory method to create a non-authenticated session with
     * automatic commit of message sends and acknowledgements and with an
     * acknowledgements batch size of 0. The returned method throws an
     * {@link ActiveMQException} if an error occurs while creating the
     * session and a {@link NullPointerException} if the input is {@code null}.
     * @return the factory method.
     */
    public static FunctionE<ClientSessionFactory, ClientSession>
    defaultSession() {
        return factory -> createSession(factory,
                                        null,     // username
                                        null,     // password
                                        false,    // xa
                                        true,     // auto commit sends
                                        true,     // auto commit acks
                                        factory.getServerLocator()
                                               .isPreAcknowledge(),
                                        0);       // ack batch size
    }
    // see note (1), (2)

    /**
     * Builds a factory method to create an authenticated session with
     * automatic commit of message sends and acknowledgements and with
     * an acknowledgements batch size of 0. The returned method throws
     * an {@link ActiveMQException} if an error occurs while creating
     * the session and a {@link NullPointerException} if the input is
     * {@code null}.
     * @param username the username to authenticate the session. Must
     *                 have at least length one.
     * @param password the password to authenticate the session. May
     *                 be empty but not {@code null}.
     * @return the factory method.
     * @throws IllegalArgumentException if the username is {@code null}
     * or empty.
     * @throws NullPointerException if the password is {@code null}.
     */
    public static FunctionE<ClientSessionFactory, ClientSession>
    defaultAuthenticatedSession(String username, String password) {
        requireString(username, "username");
        requireNonNull(password, "password");
        return factory -> createSession(factory,
                                        username,
                                        password,
                                        false,    // xa
                                        true,     // auto commit sends
                                        true,     // auto commit acks
                                        factory.getServerLocator()
                                               .isPreAcknowledge(),
                                        0);       // ack batch size
    }
    // see note (1), (2)

}
/* NOTES
 * -----
 * 1. Removal of messages from the queue.
 * When using the Artemis core API, consumed messages have to be explicitly
 * acknowledged for them to be removed from the queue. However, the core API
 * will batch ACK's and send them in one go when the configured batch size
 * is reached. This may cause consumed and acknowledged messages to linger
 * in the queue; by setting the ACK batch size to 0, we ensure messages will
 * be removed as soon as they are acknowledged.
 *
 * In fact this works exactly the same as it used to in HornetQ.
 * See
 * - http://stackoverflow.com/questions/6452505/hornetq-messages-still-remaining-in-queue-after-consuming-using-core-api
 *
 * The Artemis code that does that is in the acknowledge method of
 *
 *     org.apache.activemq.artemis.core.client.impl.ClientConsumerImpl
 *
 * which sends the ack when m messages for a total bytes of t > batch size
 * have been accumulated.
 *
 * 2. Session creation.
 * The various createSession methods of the ClientSessionFactory interface
 * are all implemented by ClientSessionFactoryImpl with a single call to
 * createSessionInternal which takes the exact same parameters as the
 * ClientSessionFactory::createSession method we call. Also note that most
 * createSession methods don't have username and password parameters, the
 * implementation in ClientSessionFactoryImpl calls createSessionInternal
 * passing in null for username and password. However, if you want to have
 * an authenticated session, using a null or empty username makes no sense,
 * so we check for that. When it comes to the password, you could use an
 * empty password, but it can't be null since security configuration gets
 * stashed away in SecurityConfiguration (*.core.config.impl package) which
 * doesn't let you use a null password or user. So we also check the password
 * isn't null in the case of authenticated sessions.
 */

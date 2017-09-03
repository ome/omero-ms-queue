package kew.providers.artemis.config.transport;

import static kew.providers.artemis.config.transport.EmbeddedTransportProps.serverId;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import util.object.Builder;
import util.types.PositiveN;

/**
 * Holds the configuration to make Artemis work with embedded connections.
 * Specifically an instance of this class configures Artemis with an "in-vm"
 * acceptor and a matching connector so that you can connect to Artemis from
 * within the same JVM process in which Artemis is embedded.
 */
public class ServerEmbeddedEndpoints implements ServerEndpointPair {

    private static final AtomicInteger currentServerId = new AtomicInteger();

    private static int nextServerId() {
        return currentServerId.incrementAndGet();
    }

    private final int embeddedServerId;
    private final EmbeddedAcceptorConfig embeddedAcceptor;
    private final EmbeddedConnectorConfig embeddedConnector;

    /**
     * Creates a new instance.
     */
    public ServerEmbeddedEndpoints() {
        embeddedServerId = nextServerId();
        embeddedAcceptor =
                makeTransport(embeddedServerId, EmbeddedAcceptorConfig::new);
        embeddedConnector =
                makeTransport(embeddedServerId, EmbeddedConnectorConfig::new);
    }

    private <T extends EndpointConfig & HasEmbeddedProps>
    T makeTransport(int embeddedServerId, Supplier<T> ctor) {
        return Builder.make(ctor)
                      .with(serverId(PositiveN.of(embeddedServerId)))
                      .apply(null);
    }

    /**
     * @return the ID shared by both the embedded acceptor and connector.
     */
    public int embeddedServerId() {
        return embeddedServerId;
    }

    @Override
    public AcceptorConfig acceptor() {
        return embeddedAcceptor;
    }

    @Override
    public ConnectorConfig connector() {
        return embeddedConnector;
    }

}
/* NOTE. Mutability.
 * Ideally we'd like to maintain these invariants:
 * - the acceptor/connector name is unique and never changes.
 * - the server ID of the acceptor = that of connector and never changes.
 * But you can always get hold of the underlying transport and shoot yourself
 * in the foot cos it's a mutable object. We could try to enforce these
 * invariants at the type-level but it's just too much work, so will rely on
 * clients behaving themselves and never change the name and server ID of the
 * underlying transport...
 */
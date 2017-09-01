package kew.providers.artemis.config.transport;

import static java.util.Objects.requireNonNull;
import static kew.providers.artemis.config.transport.EmbeddedTransportProps.serverId;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.apache.activemq.artemis.core.config.Configuration;

import util.object.Builder;
import util.types.PositiveN;

/**
 * Holds the configuration to make Artemis work with embedded connections.
 * Specifically an instance of this class configures Artemis with an "in-vm"
 * acceptor and a matching connector so that you can connect to Artemis from
 * within the same JVM process in which Artemis is embedded.
 */
public class EmbeddedServerEndpoints {

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
    public EmbeddedServerEndpoints() {
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

    /**
     * @return the embedded acceptor.
     */
    public EmbeddedAcceptorConfig acceptor() {
        return embeddedAcceptor;
    }

    /**
     * @return the embedded connector.
     */
    public EmbeddedConnectorConfig connector() {
        return embeddedConnector;
    }

    /**
     * Adds the embedded acceptor/connector pair to the specified configuration.
     * @param cfg the configuration object that will be used to start the
     *            embedded Artemis server.
     * @return the input configuration with the new settings.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public Configuration embeddedTransport(Configuration cfg) {
        requireNonNull(cfg, "cfg");

        cfg.addAcceptorConfiguration(acceptor().transport());
        cfg.addConnectorConfiguration(connector().transport().getName(),
                                      connector().transport());
        return cfg;
    }

}

package kew.providers.artemis.config;

import static java.util.Objects.requireNonNull;
import static kew.providers.artemis.config.EmbeddedTransportProps.serverId;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.config.Configuration;

import util.object.Builder;
import util.types.PositiveN;

/**
 * Holds the configuration to make Artemis work with embedded connections.
 * Specifically an instance of this class configures Artemis with an "in-vm"
 * acceptor and a matching connector so that you can connect to Artemis from
 * within the same JVM process in which Artemis is embedded.
 */
public class EmbeddedServerTransportConfig {

    private static final AtomicInteger currentServerId = new AtomicInteger();

    private static int nextServerId() {
        return currentServerId.incrementAndGet();
    }

    private final int embeddedServerId;
    private final EmbeddedTransportConfig embeddedAcceptor;
    private final EmbeddedTransportConfig embeddedConnector;

    /**
     * Creates a new instance.
     */
    public EmbeddedServerTransportConfig() {
        embeddedServerId = nextServerId();
        embeddedAcceptor = makeTransport(embeddedServerId);
        embeddedConnector = makeTransport(embeddedServerId);
    }

    private EmbeddedTransportConfig makeTransport(int embeddedServerId) {
        return Builder.make(EmbeddedTransportConfig::new)
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
    public TransportConfiguration embeddedAcceptor() {
        return embeddedAcceptor.get();
    }

    /**
     * @return the embedded connector.
     */
    public TransportConfiguration embeddedConnector() {
        return embeddedConnector.get();
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

        cfg.addAcceptorConfiguration(embeddedAcceptor());
        cfg.addConnectorConfiguration(embeddedConnector().getName(),
                                      embeddedConnector());
        return cfg;
    }

}

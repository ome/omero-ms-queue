package kew.providers.artemis.config.transport;

import static java.util.Objects.requireNonNull;

import org.apache.activemq.artemis.core.config.Configuration;

/**
 * A matching acceptor/connector configuration pair for an Artemis server.
 */
public interface ServerEndpointPair {

    /**
     * @return the acceptor.
     */
    AcceptorConfig acceptor();

    /**
     * @return the connector.
     */
    ConnectorConfig connector();

    /**
     * Adds the acceptor/connector pair to the specified Artemis configuration.
     * @param cfg the configuration object that will be used to start the
     *            Artemis server.
     * @return the input configuration with the new settings.
     * @throws NullPointerException if the argument is {@code null}.
     */
    default Configuration transportConfig(Configuration cfg) {
        requireNonNull(cfg, "cfg");

        cfg.addAcceptorConfiguration(acceptor().transport());
        cfg.addConnectorConfiguration(connector().transport().getName(),
                                      connector().transport());
        return cfg;
    }

}

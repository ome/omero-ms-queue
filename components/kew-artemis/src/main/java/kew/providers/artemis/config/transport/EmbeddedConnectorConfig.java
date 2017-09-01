package kew.providers.artemis.config.transport;

import static kew.providers.artemis.config.transport.TransportConfigFactory.makeEmbeddedConnectorTransport;

/**
 * Transport configuration for an Artemis embedded connector.
 * This is an endpoint a client uses to connect to a broker instance running
 * in the same JVM process.
 * @see EmbeddedAcceptorConfig
 */
public class EmbeddedConnectorConfig
        extends EndpointConfigAdapter
        implements ConnectorConfig, HasEmbeddedProps {

    /**
     * Creates a new Artemis transport configuration to connect to a broker
     * running in this JVM.
     */
    public EmbeddedConnectorConfig() {
        super(makeEmbeddedConnectorTransport());
    }

}

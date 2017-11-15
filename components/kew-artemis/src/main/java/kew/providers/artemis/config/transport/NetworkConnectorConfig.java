package kew.providers.artemis.config.transport;

import static kew.providers.artemis.config.transport.TransportConfigFactory.makeNetworkConnectorTransport;

/**
 * Transport configuration for an Artemis network connector.
 * This is an endpoint a client uses to connect to a broker instance over a
 * network.
 * @see NetworkAcceptorConfig
 */
public class NetworkConnectorConfig
        extends EndpointConfigAdapter
        implements ConnectorConfig, HasNetworkProps {

    /**
     * Creates a new Artemis transport configuration for a client that has to
     * connect to a broker over a network.
     */
    public NetworkConnectorConfig() {
        super(makeNetworkConnectorTransport());
    }

}

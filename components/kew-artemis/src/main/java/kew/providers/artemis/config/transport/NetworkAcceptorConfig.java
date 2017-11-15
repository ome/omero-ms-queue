package kew.providers.artemis.config.transport;

import static kew.providers.artemis.config.transport.TransportConfigFactory.makeNetworkAcceptorTransport;

/**
 * Transport configuration for an Artemis network acceptor.
 * This is an endpoint a broker instance uses to accept network connections
 * coming from remote clients.
 * @see NetworkConnectorConfig
 */
public class NetworkAcceptorConfig
        extends EndpointConfigAdapter
        implements AcceptorConfig, HasNetworkProps {

    /**
     * Creates a new Artemis transport configuration for a broker that has to
     * accept network connections.
     */
    public NetworkAcceptorConfig() {
        super(makeNetworkAcceptorTransport());
    }

}

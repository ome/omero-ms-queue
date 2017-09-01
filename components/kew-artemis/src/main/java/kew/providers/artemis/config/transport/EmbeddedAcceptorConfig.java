package kew.providers.artemis.config.transport;

import static kew.providers.artemis.config.transport.TransportConfigFactory.makeEmbeddedAcceptorTransport;

/**
 * Transport configuration for an Artemis embedded acceptor.
 * This is an endpoint a broker instance uses to accept connections coming from
 * other embedded endpoints.
 * @see EmbeddedConnectorConfig
 */
public class EmbeddedAcceptorConfig
        extends EndpointConfigAdapter
        implements AcceptorConfig, HasEmbeddedProps {

    /**
     * Creates a new Artemis transport configuration for a broker running in
     * this JVM that has to accept embedded connections.
     */
    public EmbeddedAcceptorConfig() {
        super(makeEmbeddedAcceptorTransport());
    }

}

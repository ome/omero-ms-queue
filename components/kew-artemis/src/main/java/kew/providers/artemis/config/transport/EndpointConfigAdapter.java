package kew.providers.artemis.config.transport;

import org.apache.activemq.artemis.api.core.TransportConfiguration;

import util.object.Wrapper;

/**
 * Convenience base class for {@link EndpointConfig endpoint configurations}.
 */
public abstract class EndpointConfigAdapter
        extends Wrapper<TransportConfiguration>
        implements EndpointConfig {

    protected EndpointConfigAdapter(TransportConfiguration transport) {
        super(transport);
    }

    @Override
    public TransportConfiguration transport() {
        return get();
    }

}

package kew.providers.artemis.config;

import java.util.Map;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory;

import util.object.Wrapper;

/**
 * Wraps an Artemis {@link TransportConfiguration} that should be used for
 * connecting to a broker process.
 */
public class NetworkTransportConfig extends Wrapper<TransportConfiguration> {

    private static TransportConfiguration intialConfig() {
        String fqn = NettyConnectorFactory.class.getCanonicalName();
        return new TransportConfiguration(fqn);
    }

    /**
     * Creates a new Artemis {@link TransportConfiguration} with default
     * values to be used to connect to a broker process. Before you can use
     * it, you have to configure the {@link #params() connection parameters}.
     */
    public NetworkTransportConfig() {
        super(intialConfig());
    }

    /**
     * The Artemis connection parameters. You should configure them using
     * the type-safe properties available through {@link NetworkTransportProps}.
     * @return the connection parameters, never {@code null}.
     */
    public Map<String, Object> params() {
        return get().getParams();
    }

}

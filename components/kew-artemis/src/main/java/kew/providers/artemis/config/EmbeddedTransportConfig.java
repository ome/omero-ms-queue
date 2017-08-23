package kew.providers.artemis.config;

import java.util.Map;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.remoting.impl.invm.InVMConnectorFactory;

import util.object.Wrapper;

/**
 * Wraps an Artemis {@link TransportConfiguration} that should be used for
 * connecting to a broker running within the JVM process.
 */
public class EmbeddedTransportConfig extends Wrapper<TransportConfiguration> {

    private static TransportConfiguration intialConfig() {
        String fqn = InVMConnectorFactory.class.getCanonicalName();
        return new TransportConfiguration(fqn);
    }

    /**
     * Creates a new Artemis {@link TransportConfiguration} to connect to a
     * broker running in this JVM.
     */
    public EmbeddedTransportConfig() {
        super(intialConfig());
    }

    /**
     * The Artemis connection parameters. You should configure them using
     * the type-safe properties available through {@link EmbeddedTransportProps}.
     * @return the connection parameters, never {@code null}.
     */
    public Map<String, Object> params() {
        return get().getParams();
    }

}

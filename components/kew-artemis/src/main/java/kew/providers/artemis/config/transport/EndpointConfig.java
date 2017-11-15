package kew.providers.artemis.config.transport;

import java.util.Map;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.config.Configuration;

/**
 * Represents an Artemis endpoint configuration. It can either be an acceptor
 * or a connector.
 * @see AcceptorConfig
 * @see ConnectorConfig
 */
public interface EndpointConfig {

    /**
     * Each endpoint has a unique transport configuration with a unique name.
     * This method returns that transport configuration.
     * @return the underlying transport configuration, never {@code null}.
     */
    TransportConfiguration transport();

    /**
     * The Artemis endpoint parameters. You should configure them using
     * type-safe properties available through the {@code *Props} classes.
     * @return the endpoint parameters, never {@code null}.
     */
    default Map<String, Object> params() {
        return transport().getParams();
    }

    /**
     * Adds this endpoint configuration to the Artemis main configuration.
     * If the main configuration already has a transport configuration with
     * the same name, it will be overwritten with this endpoint's configuration.
     * @param cfg the configuration object that will be used to start the
     *            Artemis server.
     * @return the input configuration with the new settings.
     * @throws NullPointerException if the argument is {@code null}.
     */
    Configuration transportConfig(Configuration cfg);

}

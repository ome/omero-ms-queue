package kew.providers.artemis.config.transport;

import static java.util.Objects.requireNonNull;

import org.apache.activemq.artemis.core.config.Configuration;

/**
 * Configuration of a connection to an Artemis sever.
 * The connection can either be a regular network connection or an embedded
 * one, i.e. one you use to connect to a broker running in the same JVM.
 */
public interface ConnectorConfig extends EndpointConfig {

    @Override
    default Configuration transportConfig(Configuration cfg) {
        requireNonNull(cfg, "cfg");

        cfg.addConnectorConfiguration(transport().getName(),  // (*)
                                      transport());
        return cfg;
    }
    /* (*) this is a map under the bonnet, so we're going to override.
     */

}

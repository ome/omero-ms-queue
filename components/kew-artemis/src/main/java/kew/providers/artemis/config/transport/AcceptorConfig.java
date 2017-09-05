package kew.providers.artemis.config.transport;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import org.apache.activemq.artemis.core.config.Configuration;

/**
 * Configuration of an Artemis sever endpoint accepting client connections.
 * This can either be a regular network acceptor or an embedded one, i.e.
 * one that accepts connections from clients running in the same JVM.
 */
public interface AcceptorConfig extends EndpointConfig {

    @Override
    default Configuration transportConfig(Configuration cfg) {
        requireNonNull(cfg, "cfg");

        if (cfg.getAcceptorConfigurations() != null) {  // (*)
            cfg.getAcceptorConfigurations()
               .removeIf(tc -> tc != null &&
                               Objects.equals(tc.getName(),
                                              transport().getName()));
        }
        cfg.addAcceptorConfiguration(transport());

        return cfg;
    }
    /* (*) Fishy Equality.
     * Artemis TransportConfiguration implements equals() in a way that two
     * instances with the same name but different params are considered equal.
     * Not sure this makes any sense, so I'm implementing a workaround of sorts
     * here so that we override any transport with the same name already in the
     * Configuration object.
     */

}

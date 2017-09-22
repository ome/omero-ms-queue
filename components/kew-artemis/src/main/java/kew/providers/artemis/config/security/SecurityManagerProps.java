package kew.providers.artemis.config.security;

import java.util.function.Consumer;

import org.apache.activemq.artemis.spi.core.security.ActiveMQSecurityManager;

import util.object.Builder;

/**
 * Properties to configure an Artemis security manager.
 * These props correspond to the JAAS security configuration block in the
 * Artemis {@code bootstrap.xml} file.
 */
public class SecurityManagerProps {

    /**
     * Instantiates a builder to configure an instance of Artemis default
     * JAAS security manager.
     * @return the builder.
     */
    public static Builder<Void, ActiveMQJAASSecurityManagerAdapter>
    defaultSecurityManager() {
        return Builder.make(ActiveMQJAASSecurityManagerAdapter::new);
    }

    /**
     * A setter to specify the Artemis "domain" entry in the JAAS
     * configuration.
     * @param name the JAAS entry name.
     * @param <T> the security manager's type.
     * @return the setter.
     * @see JaasConfigurable
     */
    public static <T extends JaasConfigurable>
    Consumer<T> domain(String name) {
        return t -> t.domain(name);
    }

    /**
     * A setter to specify the Artemis "certificate-domain" entry in the JAAS
     * configuration.
     * @param name the JAAS entry name.
     * @param <T> the security manager's type.
     * @return the setter.
     * @see JaasConfigurable
     */
    public static <T extends JaasConfigurable>
    Consumer<T> certificateDomain(String name) {
        return t -> t.certificateDomain(name);
    }

}

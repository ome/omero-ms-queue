package kew.providers.artemis.config.security;

import static util.string.Strings.requireString;

import java.util.function.Function;

import org.apache.activemq.artemis.core.config.Configuration;
import kew.providers.artemis.config.CoreConfigFactory;

/**
 * Type-safe configuration properties for security settings of an underlying
 * Artemis core configuration. Use them with a configuration builder.
 * @see CoreConfigFactory
 */
public class SecurityProps {

    /**
     * A setter to enable Artemis security.
     * @param enable {@code true} to enable security, {@code false} to disable
     *               it.
     * @return the setter.
     */
    public static Function<Configuration, Configuration>
    securityEnabled(boolean enable) {
        return cfg -> cfg.setSecurityEnabled(enable);
    }

    /**
     * A setter to specify the Artemis cluster password.
     * @param password the password to use.
     * @return the setter.
     * @throws IllegalArgumentException if the password is {@code null} or
     * empty.
     */
    public static Function<Configuration, Configuration>
    clusterPasswordOf(String password) {
        requireString(password, "password");
        return cfg -> cfg.setClusterPassword(password);
    }

}

package kew.providers.artemis.config.security;

import static java.util.Objects.requireNonNull;
import static util.string.Strings.requireString;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.security.Role;

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

    /**
     * A setter to specify role permissions for a matched address.
     * @param addressMatcher a string to match an Artemis address.
     * @param perms specifies what a user in that role can do on a
     *              matched address.
     * @return the setter.
     * @throws IllegalArgumentException if the address matcher is {@code null}
     * or empty.
     * @throws NullPointerException if the role is {@code null}.
     */
    public static Function<Configuration, Configuration>
    addressPermissionsFor(String addressMatcher, Role perms) {
        requireString(addressMatcher, "addressMatcher");
        requireNonNull(perms, "perms");

        return cfg -> {
            if (cfg.getSecurityRoles() == null) {
                cfg.setSecurityRoles(new HashMap<>());
            }
            Set<Role> rs = cfg.getSecurityRoles()
                              .getOrDefault(addressMatcher, new HashSet<>());
            rs.add(perms);
            return cfg.putSecurityRoles(addressMatcher, rs);
        };
    }

}

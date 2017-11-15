package kew.providers.artemis.config.security;

/**
 * Bridges JAAS configuration to an Artemis security manager implementation.
 * Artemis lets you configure two JAAS entries in the {@code bootstrap.xml}
 * file: the one (called the "domain") to authenticate clients over regular,
 * unencrypted connections and the other (the "certificate-domain") to use
 * SSL certificates to authenticate clients connecting through SSL. While
 * Artemis core provides the {@code ActiveMQSecurityManager} interface to
 * abstract out the authentication and access control implementation details,
 * it doesn't provide an interface for configuring the security manager
 * implementation programmatically with the "domains" you'd have specified
 * in the {@code bootstrap.xml} file. So we've rolled out our own interface
 * to fill the gap.
 */
public interface JaasConfigurable {

    /**
     * Specifies the Artemis JAAS "domain" entry name in the JAAS configuration
     * file.
     * @param name the entry name.
     * @throws IllegalArgumentException if the argument is {@code null} or
     * empty.
     */
    void domain(String name);

    /**
     * @return the "domain" entry name in the JAAS configuration.
     */
    String domain();

    /**
     * Specifies the Artemis JAAS "certificate-domain" entry name in the JAAS
     * configuration file.
     * @param name the entry name.
     * @throws IllegalArgumentException if the argument is {@code null} or
     * empty.
     */
    void certificateDomain(String name);

    /**
     * @return the "certificate-domain" entry name in the JAAS configuration.
     */
    String certificateDomain();

}

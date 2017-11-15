package kew.providers.artemis.config.security;

import static util.string.Strings.requireString;

import org.apache.activemq.artemis.spi.core.security.ActiveMQJAASSecurityManager;

/**
 * Extends {@link ActiveMQJAASSecurityManager} to make it implement the
 * {@link JaasConfigurable} interface.
 */
public class ActiveMQJAASSecurityManagerAdapter
    extends ActiveMQJAASSecurityManager
    implements JaasConfigurable {

    private String domainName;
    private String certificateDomainName;

    @Override
    public void domain(String name) {
        requireString(name, "name");
        this.domainName = name;
        setConfigurationName(name);
    }

    @Override
    public String domain() {
        return domainName;
    }

    @Override
    public void certificateDomain(String name) {
        requireString(name, "name");
        this.certificateDomainName = name;
        setCertificateConfigurationName(name);
    }

    @Override
    public String certificateDomain() {
        return certificateDomainName;
    }

}

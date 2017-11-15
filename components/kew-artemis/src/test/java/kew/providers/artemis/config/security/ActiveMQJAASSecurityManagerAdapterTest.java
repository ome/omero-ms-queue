package kew.providers.artemis.config.security;

import org.junit.Test;

public class ActiveMQJAASSecurityManagerAdapterTest {

    @Test (expected = IllegalArgumentException.class)
    public void setDomainThrowsIfNullName() {
        new ActiveMQJAASSecurityManagerAdapter().domain(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void setDomainThrowsIfEmptyName() {
        new ActiveMQJAASSecurityManagerAdapter().domain("");
    }

    @Test (expected = IllegalArgumentException.class)
    public void setCertificateDomainThrowsIfNullName() {
        new ActiveMQJAASSecurityManagerAdapter().certificateDomain(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void setCertificateDomainThrowsIfEmptyName() {
        new ActiveMQJAASSecurityManagerAdapter().certificateDomain("");
    }

}

package kew.providers.artemis.config.security;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static kew.providers.artemis.config.security.SecurityManagerProps.*;

import org.junit.Test;

public class SecurityManagerPropsTest {

    @Test
    public void buildVanillaDefaultSecurityManager() {
        ActiveMQJAASSecurityManagerAdapter target =
                defaultSecurityManager().apply(null);

        assertNotNull(target);
        assertNull(target.domain());
        assertNull(target.certificateDomain());
    }

    @Test
    public void buildDefaultSecurityManagerWithDomain() {
        String name = "d";
        ActiveMQJAASSecurityManagerAdapter target =
                defaultSecurityManager().with(domain(name))
                                        .apply(null);

        assertThat(target.domain(), is(name));
        assertNull(target.certificateDomain());
    }

    @Test
    public void buildDefaultSecurityManagerWithCertificateDomain() {
        String name = "d";
        ActiveMQJAASSecurityManagerAdapter target =
                defaultSecurityManager().with(certificateDomain(name))
                                        .apply(null);

        assertThat(target.certificateDomain(), is(name));
        assertNull(target.domain());
    }

    @Test
    public void buildDefaultSecurityManagerWithDomainAndCertificateDomain() {
        String dname = "d", cname = "c";
        ActiveMQJAASSecurityManagerAdapter target =
                defaultSecurityManager().with(certificateDomain(cname))
                                        .with(domain(dname))
                                        .apply(null);

        assertThat(target.certificateDomain(), is(cname));
        assertThat(target.domain(), is(dname));
    }

    @Test
    public void ctor() {
        new SecurityManagerProps();  // only to get 100% coverage.
    }

}

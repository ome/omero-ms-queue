package kew.providers.artemis.config.security;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static kew.providers.artemis.config.security.SecurityProps.*;

import kew.providers.artemis.config.CoreConfigFactory;
import org.apache.activemq.artemis.core.config.Configuration;

import org.junit.Test;

public class SecurityPropsTest {

    @Test
    public void enableSecurity() {
        Configuration actual = CoreConfigFactory.empty()
                                                .with(securityEnabled(true))
                                                .apply(null);
        assertTrue(actual.isSecurityEnabled());
    }

    @Test
    public void clusterPasswordSetsPassword() {
        String pass = "xxx";
        Configuration actual = CoreConfigFactory.empty()
                                                .with(clusterPasswordOf(pass))
                                                .apply(null);

        assertThat(actual.getClusterPassword(), is(pass));
    }

    @Test (expected = IllegalArgumentException.class)
    public void clusterPasswordOfThrowsIfEmptyPassword() {
        clusterPasswordOf("");
    }

    @Test (expected = IllegalArgumentException.class)
    public void clusterPasswordOfThrowsIfNullPassword() {
        clusterPasswordOf(null);
    }

    @Test
    public void ctor() {
        new SecurityProps();  // only to get 100% coverage.
    }

}

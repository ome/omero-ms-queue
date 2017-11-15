package kew.providers.artemis.runtime;

import static kew.providers.artemis.config.security.SecurityManagerProps.defaultSecurityManager;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import kew.providers.artemis.config.CoreConfigFactory;
import org.junit.Test;

import java.util.Optional;

public class DeploymentSpecTest {

    @Test
    public void automaticallyConfigureEmbeddedEndpoints() {
        DeploymentSpec target = new DeploymentSpec(
                CoreConfigFactory.empty(), defaultSecurityManager(),
                Optional.empty());

        assertNotNull(target.config());
        assertNotNull(target.embeddedEndpoints());
        assertThat(target.config().getAcceptorConfigurations(),
                   contains(target.embeddedEndpoints().acceptor().transport()));
        assertThat(target.config().getConnectorConfigurations().values(),
                   contains(target.embeddedEndpoints().connector().transport()));
    }

    @Test
    public void buildSecurityManager() {
        DeploymentSpec target = new DeploymentSpec(
                CoreConfigFactory.empty(), defaultSecurityManager(),
                Optional.empty());
        assertNotNull(target.securityManager());
    }

    @Test
    public void useDefaultMBeanServerIfNoneSpecified() {
        DeploymentSpec target = new DeploymentSpec(
                CoreConfigFactory.empty(), defaultSecurityManager(),
                Optional.empty());
        assertNotNull(target.mBeanServer());
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullConfig() {
        new DeploymentSpec(null, defaultSecurityManager(), Optional.empty());
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullSecurityManager() {
        new DeploymentSpec(CoreConfigFactory.empty(), null, Optional.empty());
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullMBeanServer() {
        new DeploymentSpec(CoreConfigFactory.empty(), defaultSecurityManager(),
                           null);
    }

}

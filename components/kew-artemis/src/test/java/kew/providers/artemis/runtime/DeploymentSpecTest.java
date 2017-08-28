package kew.providers.artemis.runtime;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import kew.providers.artemis.config.CoreConfigFactory;
import org.junit.Test;

import java.util.Optional;

public class DeploymentSpecTest {

    @Test
    public void automaticallyConfigureEmbeddedEndpoints() {
        DeploymentSpec target = new DeploymentSpec(
                CoreConfigFactory.empty(), Optional.empty(), Optional.empty());

        assertNotNull(target.config());
        assertNotNull(target.embeddedEndpoints());
        assertThat(target.config().getAcceptorConfigurations(),
                   contains(target.embeddedEndpoints().embeddedAcceptor()));
        assertThat(target.config().getConnectorConfigurations().values(),
                   contains(target.embeddedEndpoints().embeddedConnector()));
    }

    @Test
    public void useDefaultSecurityManagerIfNoneSpecified() {
        DeploymentSpec target = new DeploymentSpec(
                CoreConfigFactory.empty(), Optional.empty(), Optional.empty());
        assertNotNull(target.securityManager());
    }

    @Test
    public void useDefaultMBeanServerIfNoneSpecified() {
        DeploymentSpec target = new DeploymentSpec(
                CoreConfigFactory.empty(), Optional.empty(), Optional.empty());
        assertNotNull(target.mBeanServer());
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullConfig() {
        new DeploymentSpec(null, Optional.empty(), Optional.empty());
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullSecurityManager() {
        new DeploymentSpec(CoreConfigFactory.empty(), null, Optional.empty());
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullMBeanServer() {
        new DeploymentSpec(CoreConfigFactory.empty(), Optional.empty(), null);
    }

}

package end2end.artemis;

import static org.junit.Assert.*;
import static kew.providers.artemis.runtime.ClientSessions.*;
import static kew.providers.artemis.config.StorageProps.*;
import static kew.providers.artemis.config.security.SecurityManagerProps.*;

import java.nio.file.Path;
import java.util.Optional;

import org.apache.activemq.artemis.spi.core.security.ActiveMQSecurityManager;

import kew.providers.artemis.ServerConnector;
import kew.providers.artemis.runtime.DeploymentSpec;
import kew.providers.artemis.runtime.EmbeddedServer;
import org.apache.activemq.artemis.core.config.Configuration;
import util.object.Builder;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

public class BaseStandaloneEmbeddedServerTest {

    @Rule
    public final TemporaryFolder tempDir = new TemporaryFolder();

    protected EmbeddedServer server;

    protected <T extends ActiveMQSecurityManager>
    void start(Builder<Void, Configuration> configBuilder,
               Builder<Void, T> securityManager)
            throws Exception {
        Path dataDir = tempDir.newFolder().toPath();
        DeploymentSpec spec = new DeploymentSpec(
                configBuilder.with(defaultStorageSettings(dataDir)),
                securityManager,
                Optional.empty());
        if (spec.config().isSecurityEnabled()) {
            SecurityConfigFactory.setJaasConfig();
        }
        server = EmbeddedServer.start(spec);
    }

    protected void start(Builder<Void, Configuration> configBuilder)
            throws Exception {
        start(configBuilder, defaultSecurityManager());
    }

    protected ServerConnector startClientSession() throws Exception {
        return server.startClientSession();
    }

    protected ServerConnector startClientSession(String username,
                                                 String password)
                throws Exception {
        return server.startClientSession(
                            defaultAuthenticatedSession(username, password));
    }

    @Before
    public void setup() throws Exception {
        server = null;
    }

    @After
    public void tearDown() throws Exception {
        SecurityConfigFactory.clearJaasConfig();
        if (server != null) {
            server.stop();
            assertFalse(server.instance().isActive());
        }
    }

}

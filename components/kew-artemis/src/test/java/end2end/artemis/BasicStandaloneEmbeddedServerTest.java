package end2end.artemis;

import static org.junit.Assert.*;
import static kew.providers.artemis.config.StorageProps.defaultStorageSettings;

import kew.providers.artemis.config.CoreConfigFactory;
import kew.providers.artemis.runtime.DeploymentSpec;
import kew.providers.artemis.runtime.EmbeddedServer;
import org.apache.activemq.artemis.core.config.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import util.object.Builder;

import java.nio.file.Path;
import java.util.Optional;

public class BasicStandaloneEmbeddedServerTest {

    @Rule
    public final TemporaryFolder tempDir = new TemporaryFolder();

    private EmbeddedServer target;

    private void start(Builder<Void, Configuration> configBuilder)
            throws Exception {
        Path dataDir = tempDir.newFolder().toPath();
        DeploymentSpec spec = new DeploymentSpec(
                configBuilder.with(defaultStorageSettings(dataDir)),
                Optional.empty(),
                Optional.empty());
        target = EmbeddedServer.start(spec);
    }

    @Before
    public void setup() throws Exception {
        target = null;
    }

    @After
    public void tearDown() throws Exception {
        if (target != null) {
            target.stop();
            assertFalse(target.instance().isActive());
        }
    }

    @Test
    public void startWithBareConfig() throws Exception {
        start(CoreConfigFactory.empty());
        assertNotNull(target.instance());
        assertTrue(target.instance().isActive());
    }

    @Test
    public void sendReceiveMessage() {
        // TODO
    }

    @Test (expected = NullPointerException.class)
    public void startThrowsIfNullSpec() throws Exception {
        EmbeddedServer.start(null);
    }

}

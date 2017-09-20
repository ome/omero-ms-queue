package end2end.artemis;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static kew.providers.artemis.config.security.SecurityProps.securityEnabled;
import static kew.providers.artemis.config.StorageProps.defaultStorageSettings;
import static util.error.Exceptions.unchecked;

import kew.core.msg.ChannelSource;
import kew.providers.artemis.ServerConnector;
import kew.providers.artemis.config.CoreConfigFactory;
import kew.providers.artemis.runtime.DeploymentSpec;
import kew.providers.artemis.runtime.EmbeddedServer;
import org.apache.activemq.artemis.api.core.ActiveMQSecurityException;
import org.apache.activemq.artemis.core.config.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import util.object.Builder;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Test (expected = ActiveMQSecurityException.class)
    public void requireValidCredentialsToEstablishSession() throws Exception {
        start(CoreConfigFactory.empty()
                               .with(securityEnabled(true)));

        try (ServerConnector session = target.startClientSession()) {
            fail("shouldn't have allowed the connection!");
            session.close();  // (*)
        }
    }
    // (*) gets rid of warning about session var never being used.

    @Test
    public void sendReceiveMessage() throws Exception {
        start(CoreConfigFactory.empty()
                               .with(securityEnabled(false))
                               .with(IntQ::deploy));
        ServerConnector session = target.startClientSession();

        IntQ q = new IntQ(session);
        ChannelSource<Integer> producer = q.sourceChannel();

        QReceiveBuffer<Integer> consumer = new QReceiveBuffer<>();
        q.sinkChannel(consumer);

        Set<Integer> data = Stream.of(1, 2, 3).collect(Collectors.toSet());
        data.forEach(unchecked(producer::send));
        Set<Integer> received = consumer.waitForMessages(3, 30000);

        session.close();

        assertThat(received, is(data));
    }

    @Test (expected = NullPointerException.class)
    public void startThrowsIfNullSpec() throws Exception {
        EmbeddedServer.start(null);
    }

}

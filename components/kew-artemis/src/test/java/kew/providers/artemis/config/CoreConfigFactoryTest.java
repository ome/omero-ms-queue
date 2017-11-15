package kew.providers.artemis.config;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.server.JournalType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import util.object.Builder;

import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Set;

public class CoreConfigFactoryTest {

    private static void assertBrokerXmlContent(Configuration cfg) {
        assertNotNull(cfg);

        Map<String, TransportConfiguration> connectors =
                cfg.getConnectorConfigurations();
        assertNotNull(connectors);
        assertThat(connectors.size(), is(1));
        assertNotNull(connectors.get("in-vm"));

        Set<TransportConfiguration> acceptors = cfg.getAcceptorConfigurations();
        assertNotNull(acceptors);
        assertThat(acceptors.size(), is(1));

        assertTrue(cfg.isPersistenceEnabled());
        assertThat(cfg.getJournalType(), is(JournalType.NIO)); //(*)
    }
    /* (*) ASYNCIO warning when running this test on Linux.
     * The journal type defaults to ASYNCIO on Linux and the config parser will
     * output a warning if it can't find libaio. Seeing this message on the
     * console when running the tests could be confusing. To avoid confusion,
     * we explicitly set the journal type to NIO so the parser won't output
     * any warning.
     */

    @Rule
    public final TemporaryFolder tempDir = new TemporaryFolder();

    private URI copyBrokerXmlOutsideOfClasspath() throws Exception {
        URI sourceOnClasspath = defaultBrokerXmlFileUri();
        URI copyInTmpDir = tempDir.newFile().toURI();

        Files.copy(Paths.get(sourceOnClasspath),
                   Paths.get(copyInTmpDir),
                   StandardCopyOption.REPLACE_EXISTING);

        return copyInTmpDir;
    }

    private URI defaultBrokerXmlFileUri() throws Exception {
        String sourceOnClasspath =
                CoreConfigFactory.defaultBrokerXmlConfigLocation().toString();
        URL path = getClass().getClassLoader().getResource(sourceOnClasspath);
        // should be something like:
        // file:/your/path/to/omero-ms-queue/components/kew-artemis/build/resources/test/broker.xml

        assertNotNull(path);
        assertThat(path.getProtocol(), is("file"));

        return path.toURI();
    }

    @Test
    public void loadConfigFromFileSystemOutsideOfClasspath() throws Exception {
        URI configFile = copyBrokerXmlOutsideOfClasspath();
        Builder<Void, Configuration> builder =
                CoreConfigFactory.fromXml(configFile);
        assertNotNull(builder);

        Configuration cfg = builder.apply(null);
        assertBrokerXmlContent(cfg);
    }

    @Test
    public void canBuildDefaultBrokerXmlPath() {
        URI actual = CoreConfigFactory.defaultBrokerXmlConfigLocation();
        assertNotNull(actual);
    }

    @Test
    public void loadConfigFromDefaultLocation() {
        // should load: src/test/resources/broker.xml
        Builder<Void, Configuration> builder = CoreConfigFactory.fromXml();
        assertNotNull(builder);

        Configuration cfg = builder.apply(null);
        assertBrokerXmlContent(cfg);
    }

    @Test
    public void buildEmptyConfig() {
        Builder<Void, Configuration> builder = CoreConfigFactory.empty();
        assertNotNull(builder);

        Configuration cfg = builder.apply(null);
        assertNotNull(cfg);
    }

    @Test
    public void ctor() {
        new CoreConfigFactory();  // only to get 100% coverage.
    }

}

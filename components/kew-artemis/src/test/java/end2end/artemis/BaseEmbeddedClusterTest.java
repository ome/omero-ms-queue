package end2end.artemis;

import static org.junit.Assert.*;

import kew.providers.artemis.runtime.ClientSessions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.rules.TemporaryFolder;

import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.api.core.client.ActiveMQClient;
import org.apache.activemq.artemis.api.core.client.ServerLocator;

import kew.providers.artemis.ServerConnector;
import kew.providers.artemis.config.transport.NetworkConnectorConfig;
import kew.providers.artemis.config.transport.ServerNetworkEndpoints;
import kew.providers.artemis.runtime.ClusterWaitingRoom;
import kew.providers.artemis.runtime.DeploymentSpec;
import kew.providers.artemis.runtime.EmbeddedServer;
import util.types.PositiveN;

/**
 * Starts an embedded Artemis broker that will be part of our test cluster.
 * The test cluster is made up of this embedded broker and an external one
 * running in a separate JVM which is started in the Gradle build file.
 * @see ClusterMember
 */
public class BaseEmbeddedClusterTest {

    @ClassRule
    public static final TemporaryFolder tempDir = new TemporaryFolder();

    private static final AtomicBoolean clusterStarted = new AtomicBoolean(false);

    private static final ServerNetworkEndpoints embeddedServerEndpoints =
            ServerNetworkEndpoints.localhost(61616);
    private static final NetworkConnectorConfig externalServerConnector =
            ServerNetworkEndpoints.localhost(61617).connector();

    private static EmbeddedServer server;

    // IMPORTANT: this spec's data goes hand in hand with the values used in
    // the Gradle build file to start the external broker process.
    private static DeploymentSpec clusterSpec() throws Exception {
        String clusterPassword = "clustpass";
        Path dataDir = tempDir.newFolder().toPath();

        ClusterSpecFactory factory = new ClusterSpecFactory(
                embeddedServerEndpoints, externalServerConnector,
                clusterPassword, dataDir);
        return factory.makeSpec();
    }

    private static boolean doStartCluster() throws Exception {
        server = EmbeddedServer.start(clusterSpec());

        return new ClusterWaitingRoom(server.instance())
              .waitForClusterForming(PositiveN.of(2),
                                     Stream.of(
                                             Duration.ofMillis(200),
                                             Duration.ofMillis(1000),
                                             Duration.ofMillis(2000),
                                             Duration.ofMillis(5000)));
    }

    @BeforeClass
    public static void startCluster() throws Exception {
        if (!clusterStarted.get()) {   // (*)
            synchronized (clusterStarted) {
                if (!clusterStarted.get()) {
                    clusterStarted.set(true);

                    boolean started = doStartCluster();
                    assertTrue("cluster hasn't formed yet!", started);
                }
            }
        }
    }
    /* (*) NB JUnit may run tests concurrently.
     */

    @AfterClass
    public static void stopCluster() throws Exception {
        if (clusterStarted.get()) {   // (*)
            synchronized (clusterStarted) {
                if (clusterStarted.get()) {
                    clusterStarted.set(false);

                    server.stop();
                }
            }
        }
    }
    /* (*) NB JUnit may run tests concurrently.
     */

    protected static ServerConnector startClientSessionWithEmbeddedServer()
            throws Exception {
        return server.startClientSession();  // (*)
    }
    /* (*) Embedded Connector.
     * We use an "in-vm" connector here, instead of the network one we used
     * for the clustering config. This is still supposed to work in the case
     * of a clustered server and should avoid going through the loopback I/F.
     */

    protected static ServerConnector startClientSessionWithExternalServer()
            throws Exception {
        TransportConfiguration connector = externalServerConnector.transport();
        ServerLocator locator =
                ActiveMQClient.createServerLocatorWithHA(connector);

        return new ServerConnector(locator, ClientSessions.defaultSession());
    }

}

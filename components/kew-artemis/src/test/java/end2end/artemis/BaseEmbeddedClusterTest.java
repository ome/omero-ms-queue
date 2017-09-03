package end2end.artemis;

import static org.junit.Assert.*;

import kew.providers.artemis.config.CoreConfigFactory;
import kew.providers.artemis.config.StaticClusterConfigFactory;
import kew.providers.artemis.config.transport.ConnectorConfig;
import kew.providers.artemis.config.transport.ServerEndpointPair;
import kew.providers.artemis.config.transport.ServerNetworkEndpoints;
import kew.providers.artemis.runtime.ClusterWaitingRoom;
import kew.providers.artemis.runtime.DeploymentSpec;
import kew.providers.artemis.runtime.EmbeddedServer;
import org.apache.activemq.artemis.core.config.ClusterConnectionConfiguration;
import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.server.cluster.impl.MessageLoadBalancingType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.rules.TemporaryFolder;
import util.object.Builder;
import util.types.PositiveN;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static kew.providers.artemis.config.SecurityProps.securityEnabled;
import static kew.providers.artemis.config.StorageProps.defaultStorageSettings;

public class BaseEmbeddedClusterTest {

    private static final AtomicBoolean clusterStarted = new AtomicBoolean(false);

    @ClassRule
    public static final TemporaryFolder tempDir = new TemporaryFolder();

    protected static EmbeddedServer server1;
    protected static EmbeddedServer server2;

    private static Consumer<ClusterConnectionConfiguration>
    roundRobinLoadBalancing() {
        return cfg -> {
            cfg.setMaxHops(1);
            cfg.setMessageLoadBalancingType(MessageLoadBalancingType.STRICT);
        };
    }

    private static Function<Configuration, Configuration> clusteringConfig(
            ConnectorConfig member, ConnectorConfig topologyMaster) {
        return new StaticClusterConfigFactory(topologyMaster)
              .clusterConfig(roundRobinLoadBalancing(), member);
    }

    private static Builder<Void, Configuration>
    embeddedSymClusterWithIntQConfigBuilder(
            ServerEndpointPair memberEndpoints,
            ConnectorConfig topologyMaster)
                throws Exception {
        Path dataDir = tempDir.newFolder().toPath();

        return CoreConfigFactory
              .empty()
              .with(defaultStorageSettings(dataDir))
              .with(securityEnabled(false))
              .with(memberEndpoints::transportConfig)
              .with(clusteringConfig(memberEndpoints.connector(),
                                     topologyMaster))
              .with(IntQ::deploy);
    }

    private static DeploymentSpec embeddedSymClusterWithIntQ(
            ServerEndpointPair memberEndpoints,
            ConnectorConfig topologyMaster)
                throws Exception {
        return new DeploymentSpec(
                embeddedSymClusterWithIntQConfigBuilder(memberEndpoints,
                                                        topologyMaster),
                Optional.empty(),
                Optional.empty());
    }

    protected static ServerNetworkEndpoints endpoints1;
    protected static ServerNetworkEndpoints endpoints2;

    private static boolean doStartCluster() throws Exception {
        //ServerNetworkEndpoints endpoints1 =
        endpoints1 =
                ServerNetworkEndpoints.localhost(61616);
        //ServerNetworkEndpoints endpoints2 =
        endpoints2 =
                ServerNetworkEndpoints.localhost(61617);

        server1 = EmbeddedServer.start(
                embeddedSymClusterWithIntQ(endpoints1,
                                           endpoints2.connector()));
        server2 = EmbeddedServer.start(
                embeddedSymClusterWithIntQ(endpoints2,
                                           endpoints1.connector()));
/*
        return new ClusterWaitingRoom(server2.instance())
              .waitForClusterForming(PositiveN.of(2),
                                     Stream.of(
                                             Duration.ofMillis(200),
                                             Duration.ofMillis(1 * 1000),
                                             Duration.ofMillis(2 * 1000),
                                             Duration.ofMillis(5 * 1000)));
                                             */
        return true;
    }

    @BeforeClass
    public static void startCluster() throws Exception {
        if (!clusterStarted.get()) {   // (*)
            synchronized (clusterStarted) {
                if (!clusterStarted.get()) {
                    clusterStarted.set(true);

                    boolean started = doStartCluster();
                    //assertTrue(started);
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

                    server1.stop();
                    server2.stop();
                }
            }
        }
    }
    /* (*) NB JUnit may run tests concurrently.
     */
}

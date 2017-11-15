package end2end.artemis;

import java.nio.file.Path;
import java.nio.file.Paths;

import kew.providers.artemis.config.transport.NetworkConnectorConfig;
import kew.providers.artemis.config.transport.ServerNetworkEndpoints;
import kew.providers.artemis.runtime.EmbeddedServer;

/**
 * Starts an Artemis broker that will be part of our test cluster.
 * This is supposed to run in a process external to the JUnit runner. We start
 * a fresh JVM to run this broker as part of our end-to-end tests set up in
 * the Gradle build file.
 */
public class ClusterMember {

    private static void start(int port, int topologyMasterPort,
                              String clusterPassword, Path dataDir)
            throws Exception {
        ServerNetworkEndpoints endpoints =
                ServerNetworkEndpoints.localhost(port);
        NetworkConnectorConfig topologyMaster =
                ServerNetworkEndpoints.localhost(topologyMasterPort)
                                      .connector();
        ClusterSpecFactory csf = new ClusterSpecFactory(endpoints,
                                                        topologyMaster,
                                                        clusterPassword,
                                                        dataDir);
        EmbeddedServer.start(csf.makeSpec());
    }

    /**
     * Starts the Artemis broker on the port specified as first argument.
     * The second argument is the local port of the other cluster member
     * from which this one will download the cluster topology. The third
     * argument is the cluster password. The fourth and last argument is
     * the temp data directory where this broker will store its data.
     * @param argv the arguments as detailed above.
     * @throws Exception if a start up error occurs.
     */
    public static void main(String[] argv) throws Exception {
        int port = Integer.parseInt(argv[0]);
        int topologyMasterPort = Integer.parseInt(argv[1]);
        String clusterPassword = argv[2];
        Path dataDir = Paths.get(argv[3]);
        start(port, topologyMasterPort, clusterPassword, dataDir);
    }

}

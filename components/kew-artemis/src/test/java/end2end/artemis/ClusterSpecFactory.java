package end2end.artemis;

import static kew.providers.artemis.config.security.SecurityProps.*;
import static kew.providers.artemis.config.StorageProps.defaultStorageSettings;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.activemq.artemis.core.config.ClusterConnectionConfiguration;
import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.server.cluster.impl.MessageLoadBalancingType;

import kew.providers.artemis.config.CoreConfigFactory;
import kew.providers.artemis.config.cluster.StaticClusterConfigFactory;
import kew.providers.artemis.config.transport.NetworkConnectorConfig;
import kew.providers.artemis.config.transport.ServerNetworkEndpoints;
import kew.providers.artemis.runtime.DeploymentSpec;
import util.object.Builder;

/**
 * Factory to create an Artemis configuration for a member of our test cluster.
 */
public class ClusterSpecFactory {

    private static Consumer<ClusterConnectionConfiguration>
    roundRobinLoadBalancing() {
        return cfg -> {
            cfg.setMaxHops(1);
            cfg.setMessageLoadBalancingType(MessageLoadBalancingType.STRICT);
        };
    }


    private final ServerNetworkEndpoints memberEndpoints;
    private final NetworkConnectorConfig topologyMaster;
    private final String clusterPassword;
    private final Path dataDir;

    public ClusterSpecFactory(ServerNetworkEndpoints memberEndpoints,
                              NetworkConnectorConfig topologyMaster,
                              String clusterPassword,
                              Path dataDir) {
        this.memberEndpoints = memberEndpoints;
        this.topologyMaster = topologyMaster;
        this.clusterPassword = clusterPassword;
        this.dataDir = dataDir;
    }

    private Function<Configuration, Configuration> clusteringConfig() {
        return new StaticClusterConfigFactory(topologyMaster)
              .clusterConfig(roundRobinLoadBalancing(),
                             memberEndpoints);
    }

    private Builder<Void, Configuration> makeBuilder() throws Exception {
        return CoreConfigFactory
                .empty()
                .with(defaultStorageSettings(dataDir))
                .with(securityEnabled(false))
                .with(clusterPasswordOf(clusterPassword))
                .with(clusteringConfig())
                .with(IntQ::deploy);
    }

    public DeploymentSpec makeSpec() throws Exception {
        return new DeploymentSpec(makeBuilder(),
                                  Optional.empty(),
                                  Optional.empty());
    }

}

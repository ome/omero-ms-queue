package kew.providers.artemis.config;

import static java.util.stream.Collectors.toList;
import static util.sequence.Arrayz.requireArray;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.apache.activemq.artemis.core.config.ClusterConnectionConfiguration;

import kew.providers.artemis.config.transport.ConnectorConfig;

/**
 * A {@link ClusterConfigFactory} that downloads the cluster topology from
 * a fixed list of Artemis servers in the cluster.
 */
public class StaticClusterConfigFactory extends BaseClusterConfigFactory {


    private final ConnectorConfig[] connectorsToTopologyMasters;

    /**
     * Creates a new instance.
     * @param connectorsToTopologyMasters connections to the cluster members
     * from which to download the cluster topology.
     * @throws IllegalArgumentException if the connectors array is {@code null},
     * or it has zero length, or some of its elements are {@code null}.
     */
    public StaticClusterConfigFactory(
            ConnectorConfig...connectorsToTopologyMasters) {
        requireArray(connectorsToTopologyMasters);
        this.connectorsToTopologyMasters = connectorsToTopologyMasters;
    }

    private List<String> connectorsToTopologyMastersNames() {
        return Stream.of(connectorsToTopologyMasters)
                     .map(c -> c.transport().getName())
                     .collect(toList());
    }

    @Override
    protected ClusterConnectionConfiguration buildConnection(
            Consumer<ClusterConnectionConfiguration> customizer,
            ConnectorConfig connector) {
        ClusterConnectionConfiguration cfg =
                super.buildConnection(customizer, connector);

        cfg.setStaticConnectors(connectorsToTopologyMastersNames());

        return cfg;
    }

}

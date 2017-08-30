package kew.providers.artemis.config;

import static java.util.stream.Collectors.toList;
import static util.sequence.Arrayz.requireArray;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.config.ClusterConnectionConfiguration;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * A {@link ClusterConfigFactory} that downloads the cluster topology from
 * a fixed list of Artemis servers in the cluster.
 */
public class StaticClusterConfigFactory extends BaseClusterConfigFactory {


    private final TransportConfiguration[] connectorsToTopologyMasters;

    /**
     * Creates a new instance.IllegalArgumentException
     * @param connectorsToTopologyMasters connections to the cluster members
     * from which to download the cluster topology.
     * @throws IllegalArgumentException if the connectors array is {@code null},
     * or it has zero length, or some of its elements are {@code null}.
     */
    public StaticClusterConfigFactory(
            TransportConfiguration...connectorsToTopologyMasters) {
        requireArray(connectorsToTopologyMasters);
        this.connectorsToTopologyMasters = connectorsToTopologyMasters;
    }

    private List<String> connectorsToTopologyMastersNames() {
        return Stream.of(connectorsToTopologyMasters)
                     .map(TransportConfiguration::getName)
                     .collect(toList());
    }

    @Override
    protected ClusterConnectionConfiguration buildConnection(
            Consumer<ClusterConnectionConfiguration> customizer,
            TransportConfiguration connector) {
        ClusterConnectionConfiguration cfg =
                super.buildConnection(customizer, connector);

        cfg.setStaticConnectors(connectorsToTopologyMastersNames());

        return cfg;
    }

}

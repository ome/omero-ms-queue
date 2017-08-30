package kew.providers.artemis.config;

import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.config.ClusterConnectionConfiguration;
import org.apache.activemq.artemis.core.config.Configuration;

/**
 * Builds functions to add cluster settings to a core Artemis configuration.
 * Cluster settings vary depending on how the cluster topology is propagated
 * among cluster members: Artemis lets you use UDP broadcast, JGroups, or
 * even a static list of cluster members from which the others can retrieve
 * initial topology and subsequent updates. Each implementing class provides
 * the configuration for a specific topology propagation strategy as well as
 * connector cluster configuration proper---i.e. the enabling of clustering
 * with one or more connectors.
 */
public interface ClusterConfigFactory {

    /**
     * Builds a function that adds cluster settings to a given Artemis core
     * configuration. If you pass in an empty list of connectors, then the
     * returned function will do nothing. Any duplicated connectors will be
     * automatically removed.
     * @param customizer sets specific cluster connection parameters if the
     *                   default settings are not suitable.
     * @param connectors one or more connectors other cluster members should
     *                  use to connect to this server.
     * @return the function to add cluster settings.
     * @throws NullPointerException if the customizer is {@code null}.
     * @throws IllegalArgumentException if the connectors is {@code null} or
     * it has {@code null}s in it.
     */
    Function<Configuration, Configuration> clusterConfig(
            Consumer<ClusterConnectionConfiguration> customizer,
            TransportConfiguration...connectors);

    /**
     * Builds a function that adds cluster settings to a given Artemis core
     * configuration. If you pass in an empty list of connectors, then the
     * returned function will do nothing. Any duplicated connectors will be
     * automatically removed.
     * @param connectors one or more connectors other cluster members should
     *                  use to connect to this server.
     * @return the function to add cluster settings.
     * @throws IllegalArgumentException if the connectors is {@code null} or
     * it has {@code null}s in it.
     */
    default Function<Configuration, Configuration> clusterConfig(
            TransportConfiguration...connectors) {
        return clusterConfig(c -> {}, connectors);
    }

}
/* NOTE. Improving Type-safety.
 * How do we know a TransportConfiguration is for a connector rather than an
 * acceptor? We don't. (See note about type-safety in NetworkTransportProps!)
 * Perhaps we could have Acceptor/Connector types to rule out configuration
 * mistakes...
 */
package kew.providers.artemis.config;

import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.activemq.artemis.core.config.ClusterConnectionConfiguration;
import org.apache.activemq.artemis.core.config.Configuration;

import kew.providers.artemis.config.transport.ServerNetworkEndpoints;

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
     * configuration. For each endpoint pair, we add both the connector and
     * acceptor to the core configuration and then use the connector to add
     * a cluster configuration too. If you pass in an empty list of endpoints,
     * then the returned function will do nothing.
     * @param customizer sets specific cluster connection parameters if the
     *                   default settings are not suitable.
     * @param endpointsPairs one or more acceptors/connectors other cluster
     *                       members should use to connect to this server.
     * @return the function to add cluster settings.
     * @throws NullPointerException if the customizer is {@code null}.
     * @throws IllegalArgumentException if the connectors is {@code null} or
     * it has {@code null}s in it.
     */
    Function<Configuration, Configuration> clusterConfig(
            Consumer<ClusterConnectionConfiguration> customizer,
            ServerNetworkEndpoints...endpointsPairs);

    /**
     * Builds a function that adds cluster settings to a given Artemis core
     * configuration. For each endpoint pair, we add both the connector and
     * acceptor to the core configuration and then use the connector to add
     * a cluster configuration too. If you pass in an empty list of endpoints,
     * then the returned function will do nothing.
     * @param endpointsPairs one or more acceptors/connectors other cluster
     *                       members should use to connect to this server.
     * @return the function to add cluster settings.
     * @throws IllegalArgumentException if the connectors is {@code null} or
     * it has {@code null}s in it.
     */
    default Function<Configuration, Configuration> clusterConfig(
            ServerNetworkEndpoints...endpointsPairs) {
        return clusterConfig(c -> {}, endpointsPairs);
    }

}

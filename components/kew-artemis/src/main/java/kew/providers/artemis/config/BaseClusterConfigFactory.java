package kew.providers.artemis.config;

import static java.util.Objects.requireNonNull;
import static util.sequence.Arrayz.asList;
import static util.sequence.Arrayz.requireArrayOfMinLength;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.activemq.artemis.core.config.ClusterConnectionConfiguration;
import org.apache.activemq.artemis.core.config.Configuration;

import kew.providers.artemis.config.transport.NetworkConnectorConfig;
import kew.providers.artemis.config.transport.ServerNetworkEndpoints;
import util.types.UuidString;

/**
 * Convenience base class for implementing a {@link ClusterConfigFactory}.
 * It configures clustering with one or more connectors so that subclasses
 * can just configure a topology propagation strategy.
 */
public abstract class BaseClusterConfigFactory implements ClusterConfigFactory {

    protected ClusterConnectionConfiguration buildConnection(
            Consumer<ClusterConnectionConfiguration> customizer,
            NetworkConnectorConfig connector) {
        ClusterConnectionConfiguration cfg =
                new ClusterConnectionConfiguration();

        customizer.accept(cfg);  // (*)
        cfg.setName(new UuidString().id());
        cfg.setConnectorName(connector.transport().getName());

        return cfg;
    }
    /* (*) we call the customizer first to avoid it accidentally overriding
     * the linking of the connector.
     */

    protected Configuration buildConfig(
            Configuration cfg,
            Consumer<ClusterConnectionConfiguration> customizer,
            List<ServerNetworkEndpoints> endpointsPairs) {
        endpointsPairs.forEach(endpoint -> {
            endpoint.transportConfig(cfg);
            ClusterConnectionConfiguration c =
                    buildConnection(customizer, endpoint.connector());
            cfg.addClusterConfiguration(c);
        });
        return cfg;
    }

    @Override
    public Function<Configuration, Configuration> clusterConfig(
            Consumer<ClusterConnectionConfiguration> customizer,
            ServerNetworkEndpoints...endpointsPairs) {
        requireNonNull(customizer, "customizer");
        requireArrayOfMinLength(0, endpointsPairs);

        return cfg -> buildConfig(cfg, customizer, asList(endpointsPairs));
    }

}

package kew.providers.artemis.config;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;
import static util.sequence.Arrayz.asStream;
import static util.sequence.Arrayz.requireArrayOfMinLength;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.config.ClusterConnectionConfiguration;
import org.apache.activemq.artemis.core.config.Configuration;

import util.types.UuidString;

/**
 * Convenience base class for implementing a {@link ClusterConfigFactory}.
 * It configures clustering with one or more connectors so that subclasses
 * can just configure a topology propagation strategy.
 */
public abstract class BaseClusterConfigFactory implements ClusterConfigFactory {

    protected ClusterConnectionConfiguration buildConnection(
            Consumer<ClusterConnectionConfiguration> customizer,
            TransportConfiguration connector) {
        ClusterConnectionConfiguration cfg =
                new ClusterConnectionConfiguration();

        customizer.accept(cfg);  // (*)
        cfg.setName(new UuidString().id());
        cfg.setConnectorName(connector.getName());

        return cfg;
    }
    /* (*) we call the customizer first to avoid it accidentally overriding
     * the linking of the connector.
     */

    protected Configuration buildConfig(
            Configuration cfg,
            Consumer<ClusterConnectionConfiguration> customizer,
            Set<TransportConfiguration> connectors) {
        connectors.forEach(connector -> {
            ClusterConnectionConfiguration c = buildConnection(customizer,
                                                               connector);
            cfg.addClusterConfiguration(c);
        });
        return cfg;
    }

    @Override
    public Function<Configuration, Configuration> clusterConfig(
            Consumer<ClusterConnectionConfiguration> customizer,
            TransportConfiguration...connectors) {
        requireNonNull(customizer, "customizer");
        requireArrayOfMinLength(0, connectors);

        Set<TransportConfiguration> cs = asStream(connectors)
                                        .collect(toSet());
        return cfg -> buildConfig(cfg, customizer, cs);
    }

}

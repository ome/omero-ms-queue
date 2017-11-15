package kew.providers.artemis.config.cluster;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import kew.providers.artemis.config.CoreConfigFactory;
import kew.providers.artemis.config.transport.*;
import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.config.Configuration;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StaticClusterConfigFactoryTest {

    @Test
    public void linkConnectorsToTopologyMasters() {
        ServerNetworkEndpoints sne = ServerNetworkEndpoints.localhost(1);
        NetworkConnectorConfig master1 = new NetworkConnectorConfig();
        NetworkConnectorConfig master2 = new NetworkConnectorConfig();
        Configuration config = CoreConfigFactory.empty().apply(null);

        new StaticClusterConfigFactory(master1, master2).clusterConfig(sne)
                                                        .apply(config);

        assertThat(config.getClusterConfigurations(), hasSize(1));

        Set<String> actualConnectorNames = new HashSet<>(
                config.getClusterConfigurations()
                      .get(0)
                      .getStaticConnectors()
        );
        Set<String> expectedConnectorNames =
                Stream.of(master1, master2)
                      .map(EndpointConfig::transport)
                      .map(TransportConfiguration::getName)
                      .collect(Collectors.toSet());

        assertThat(actualConnectorNames, is(expectedConnectorNames));
    }

    @Test
    public void addTopologyMastersToConfiguredConnectors() {
        ServerNetworkEndpoints sne = ServerNetworkEndpoints.localhost(1);
        NetworkConnectorConfig master = new NetworkConnectorConfig();
        Configuration config = CoreConfigFactory.empty().apply(null);

        new StaticClusterConfigFactory(master).clusterConfig(sne)
                                              .apply(config);

        Set<TransportConfiguration> expected = Stream
                                              .of(sne.connector(), master)
                                              .map(EndpointConfig::transport)
                                              .collect(Collectors.toSet());
        Set<TransportConfiguration> actual = new HashSet<>(
                config.getConnectorConfigurations().values()
        );
        assertThat(actual, is(expected));
    }

    @Test (expected = IllegalArgumentException.class)
    public void ctorThrowsIfNullConnectors() {
        new StaticClusterConfigFactory((NetworkConnectorConfig[]) null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void ctorThrowsIfEmptyConnectors() {
        new StaticClusterConfigFactory();
    }

    @Test (expected = IllegalArgumentException.class)
    public void ctorThrowsIfSomeConnectorsAreNull() {
        new StaticClusterConfigFactory(new NetworkConnectorConfig(), null);
    }

}

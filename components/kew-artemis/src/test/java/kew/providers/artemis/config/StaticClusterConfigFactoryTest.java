package kew.providers.artemis.config;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

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
        ConnectorConfig net = new NetworkConnectorConfig();
        ConnectorConfig inVm = new EmbeddedConnectorConfig();
        Configuration config = CoreConfigFactory.empty().apply(null);

        new StaticClusterConfigFactory(net, inVm).clusterConfig(net)
                                                 .apply(config);

        assertThat(config.getClusterConfigurations(), hasSize(1));

        Set<String> actualConnectorNames = new HashSet<>(
                config.getClusterConfigurations()
                        .get(0)
                        .getStaticConnectors()
        );
        Set<String> expectedConnectorNames =
                Stream.of(net, inVm)
                      .map(EndpointConfig::transport)
                      .map(TransportConfiguration::getName)
                      .collect(Collectors.toSet());

        assertThat(actualConnectorNames, is(expectedConnectorNames));
    }

    @Test (expected = IllegalArgumentException.class)
    public void ctorThrowsIfNullConnectors() {
        new StaticClusterConfigFactory((ConnectorConfig[]) null);
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

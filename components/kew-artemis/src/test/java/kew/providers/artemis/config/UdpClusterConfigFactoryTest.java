package kew.providers.artemis.config;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.apache.activemq.artemis.api.core.BroadcastGroupConfiguration;
import org.apache.activemq.artemis.api.core.DiscoveryGroupConfiguration;
import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.api.core.UDPBroadcastEndpointFactory;
import org.apache.activemq.artemis.core.config.Configuration;
import org.junit.Test;
import util.types.PositiveN;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UdpClusterConfigFactoryTest {

    private Configuration buildConfig(TransportConfiguration...connectors) {
        Configuration config = CoreConfigFactory.empty().apply(null);

        return new UdpClusterConfigFactory("231.7.7.7", PositiveN.of(9876))
              .clusterConfig(connectors)
              .apply(config);
    }

    @Test
    public void buildOnlyOneBroadcastGroup() {
        TransportConfiguration net = new NetworkTransportConfig().get();
        TransportConfiguration inVm = new EmbeddedTransportConfig().get();
        Configuration config = buildConfig(net, inVm);

        assertThat(config.getBroadcastGroupConfigurations(), hasSize(1));
    }

    @Test
    public void buildOnlyOneDiscoveryGroup() {
        TransportConfiguration net = new NetworkTransportConfig().get();
        TransportConfiguration inVm = new EmbeddedTransportConfig().get();
        Configuration config = buildConfig(net, inVm);

        assertNotNull(config.getDiscoveryGroupConfigurations());
        assertThat(config.getDiscoveryGroupConfigurations().values(),
                   hasSize(1));
    }

    @Test
    public void linkConnectorsToBroadcastGroup() {
        TransportConfiguration net = new NetworkTransportConfig().get();
        TransportConfiguration inVm = new EmbeddedTransportConfig().get();
        Configuration config = buildConfig(net, inVm);

        Set<String> actualConnectorNames = new HashSet<>(
                config.getBroadcastGroupConfigurations()
                      .get(0)
                      .getConnectorInfos()
        );
        Set<String> expectedConnectorNames =
                Stream.of(net, inVm)
                      .map(TransportConfiguration::getName)
                      .collect(Collectors.toSet());

        assertThat(actualConnectorNames, is(expectedConnectorNames));
    }

    @Test
    public void useMutableListOfConnectorNamesInBroadcastGroup() {
        TransportConfiguration net = new NetworkTransportConfig().get();
        Configuration config = buildConfig(net);

        List<String> connectorNames = config.getBroadcastGroupConfigurations()
                                            .get(0)
                                            .getConnectorInfos();

        assertThat(connectorNames, hasSize(1));
        connectorNames.add("xxx");
        assertThat(connectorNames, hasSize(2));
    }

    @Test
    public void discoveryAndBroadcastGroupsHaveSameEndpoint() {
        TransportConfiguration net = new NetworkTransportConfig().get();
        Configuration config = buildConfig(net);

        BroadcastGroupConfiguration broadcast =
                config.getBroadcastGroupConfigurations().get(0);
        DiscoveryGroupConfiguration discovery =
                config.getDiscoveryGroupConfigurations()
                      .values().stream().findFirst().orElse(null);

        assertNotNull(broadcast.getEndpointFactory());
        assertNotNull(discovery.getBroadcastEndpointFactory());

        UDPBroadcastEndpointFactory broadcastEndpoint =
                (UDPBroadcastEndpointFactory) broadcast.getEndpointFactory();
        UDPBroadcastEndpointFactory discoveryEndpoint =
                (UDPBroadcastEndpointFactory) broadcast.getEndpointFactory();

        assertThat(broadcastEndpoint.getGroupAddress(),
                   is(discoveryEndpoint.getGroupAddress()));
        assertThat(broadcastEndpoint.getGroupPort(),
                is(discoveryEndpoint.getGroupPort()));
    }

    @Test (expected = IllegalArgumentException.class)
    public void ctorThrowsIfNullIpAddress() {
        new UdpClusterConfigFactory(null, PositiveN.of(1));
    }

    @Test (expected = IllegalArgumentException.class)
    public void ctorThrowsIfEmptyIpAddress() {
        new UdpClusterConfigFactory("", PositiveN.of(1));
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullPort() {
        new UdpClusterConfigFactory("1.1.1.1", null);
    }

}

package kew.providers.artemis.config.cluster;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import kew.providers.artemis.config.CoreConfigFactory;
import kew.providers.artemis.config.transport.*;
import org.apache.activemq.artemis.api.core.BroadcastGroupConfiguration;
import org.apache.activemq.artemis.api.core.DiscoveryGroupConfiguration;
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

    private Configuration buildConfig(ServerNetworkEndpoints...connectors) {
        Configuration config = CoreConfigFactory.empty().apply(null);

        return new UdpClusterConfigFactory("231.7.7.7", PositiveN.of(9876))
              .clusterConfig(connectors)
              .apply(config);
    }

    @Test
    public void buildOnlyOneBroadcastGroup() {
        ServerNetworkEndpoints e1 = ServerNetworkEndpoints.localhost(1);
        ServerNetworkEndpoints e2 = ServerNetworkEndpoints.localhost(2);
        Configuration config = buildConfig(e1, e2);

        assertThat(config.getBroadcastGroupConfigurations(), hasSize(1));
    }

    @Test
    public void buildOnlyOneDiscoveryGroup() {
        ServerNetworkEndpoints e1 = ServerNetworkEndpoints.localhost(1);
        ServerNetworkEndpoints e2 = ServerNetworkEndpoints.localhost(2);
        Configuration config = buildConfig(e1, e2);

        assertNotNull(config.getDiscoveryGroupConfigurations());
        assertThat(config.getDiscoveryGroupConfigurations().values(),
                   hasSize(1));
    }

    @Test
    public void linkConnectorsToBroadcastGroup() {
        ServerNetworkEndpoints e1 = ServerNetworkEndpoints.localhost(1);
        ServerNetworkEndpoints e2 = ServerNetworkEndpoints.localhost(2);
        Configuration config = buildConfig(e1, e2);

        Set<String> actualConnectorNames = new HashSet<>(
                config.getBroadcastGroupConfigurations()
                      .get(0)
                      .getConnectorInfos()
        );
        Set<String> expectedConnectorNames =
                Stream.of(e1, e2)
                      .map(e -> e.connector().transport().getName())
                      .collect(Collectors.toSet());

        assertThat(actualConnectorNames, is(expectedConnectorNames));
    }

    @Test
    public void useMutableListOfConnectorNamesInBroadcastGroup() {
        ServerNetworkEndpoints sne = ServerNetworkEndpoints.localhost(1);
        Configuration config = buildConfig(sne);

        List<String> connectorNames = config.getBroadcastGroupConfigurations()
                                            .get(0)
                                            .getConnectorInfos();

        assertThat(connectorNames, hasSize(1));
        connectorNames.add("xxx");
        assertThat(connectorNames, hasSize(2));
    }

    @Test
    public void discoveryAndBroadcastGroupsHaveSameEndpoint() {
        ServerNetworkEndpoints sne = ServerNetworkEndpoints.localhost(1);
        Configuration config = buildConfig(sne);

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

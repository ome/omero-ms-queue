package kew.providers.artemis.config;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static util.string.Strings.requireString;

import java.util.List;
import java.util.function.Consumer;

import org.apache.activemq.artemis.api.core.*;
import org.apache.activemq.artemis.core.config.ClusterConnectionConfiguration;
import org.apache.activemq.artemis.core.config.Configuration;

import kew.providers.artemis.config.transport.NetworkConnectorConfig;
import kew.providers.artemis.config.transport.ServerNetworkEndpoints;
import util.types.PositiveN;
import util.types.UuidString;

/**
 * A {@link ClusterConfigFactory} that uses UDP multi-cast to download the
 * initial cluster topology from some Artemis server in the cluster. Note
 * that subsequent topology updates are propagated among cluster members
 * using regular Artemis client/server TCP connections; UDP multi-cast is
 * only used initially to figure out how to locate some of the other cluster
 * members.
 */
public class UdpClusterConfigFactory extends BaseClusterConfigFactory {

    private static BroadcastEndpointFactory makeFactory(String ipAddress,
                                                        int port) {
        return new UDPBroadcastEndpointFactory()
              .setGroupAddress(ipAddress)
              .setGroupPort(port);
    }

    private static DiscoveryGroupConfiguration makeDiscoveryGroup(
            String ipAddress, int port) {
        return new DiscoveryGroupConfiguration()
              .setName(new UuidString().id())
              .setBroadcastEndpointFactory(makeFactory(ipAddress, port));
    }
    // see parseDiscoveryGroupConfiguration of FileConfigurationParser in
    // org.apache.activemq.artemis.core.deployers.impl package.

    private static BroadcastGroupConfiguration makeBroadcastGroup(
            String ipAddress, int port, List<String> connectorNames) {
        return new BroadcastGroupConfiguration()
              .setName(new UuidString().id())
              .setConnectorInfos(connectorNames)
              .setEndpointFactory(makeFactory(ipAddress, port));
    }
    // see parseBroadcastGroupConfiguration of FileConfigurationParser.

    private static List<String> connectorNames(
            List<ServerNetworkEndpoints> endpointsPairs) {
        return endpointsPairs.stream()
                             .map(ServerNetworkEndpoints::connector)
                             .map(c -> c.transport().getName())
                             .collect(toList());
    }


    private final String ipAddress;  // should we rather use InetAddres?
    private final PositiveN port;
    private final DiscoveryGroupConfiguration discoveryGroup;

    /**
     * Creates a new instance.
     * @param ipAddress the IP multi-cast address to use, e.g. "231.7.7.7".
     * @param port the multi-cast port to use, e.g. 9876.
     * @throws IllegalArgumentException if the IP address is {@code null} or
     * empty.
     * @throws NullPointerException if the port is {@code null}.
     */
    public UdpClusterConfigFactory(String ipAddress, PositiveN port) {
        requireString(ipAddress, "ipAddress");
        requireNonNull(port, "port");

        this.ipAddress = ipAddress;
        this.port = port;
        this.discoveryGroup = makeDiscoveryGroup(ipAddress,
                                                 port.get().intValue());
    }

    @Override
    protected ClusterConnectionConfiguration buildConnection(
            Consumer<ClusterConnectionConfiguration> customizer,
            NetworkConnectorConfig connector) {
        ClusterConnectionConfiguration cfg =
                super.buildConnection(customizer, connector);
        cfg.setDiscoveryGroupName(discoveryGroup.getName());

        return cfg;
    }

    @Override
    protected Configuration buildConfig(
            Configuration cfg,
            Consumer<ClusterConnectionConfiguration> customizer,
            List<ServerNetworkEndpoints> endpointsPairs) {
        cfg.addDiscoveryGroupConfiguration(discoveryGroup.getName(),
                                           discoveryGroup);
        BroadcastGroupConfiguration bgc =
                makeBroadcastGroup(ipAddress,
                                   port.get().intValue(),
                                   connectorNames(endpointsPairs));
        cfg.addBroadcastGroupConfiguration(bgc);

        return super.buildConfig(cfg, customizer, endpointsPairs);
    }

}
/* NOTE. Customisation.
 * If needed we could add customizers for broadcast/discovery groups e.g.
 * using consumers passed in to the UdpClusterConfigFactory constructor.
 */
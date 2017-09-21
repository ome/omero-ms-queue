package kew.providers.artemis.config.cluster;

import static java.util.stream.Collectors.toSet;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import kew.providers.artemis.config.CoreConfigFactory;
import kew.providers.artemis.config.transport.ServerNetworkEndpoints;
import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.config.ClusterConnectionConfiguration;
import org.apache.activemq.artemis.core.config.Configuration;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class BaseClusterConfigFactoryTest
        extends BaseClusterConfigFactory
        implements Consumer<ClusterConnectionConfiguration> {

    private boolean customizerCalled;

    @Override
    public void accept(ClusterConnectionConfiguration cfg) {
        cfg.setName("");
        cfg.setConnectorName("");
        customizerCalled = true;
    }

    @Before
    public void setup() {
        customizerCalled = false;
    }

    private Configuration buildConfig(ServerNetworkEndpoints...connectors) {
        Configuration config = CoreConfigFactory.empty().apply(null);
        clusterConfig(this, connectors).apply(config);
        return config;
    }

    @Test
    public void doNothingIfEmptyEndpoints() {
        Function<Configuration, Configuration> target = clusterConfig(this);
        Configuration config = mock(Configuration.class);
        target.apply(config);

        verifyZeroInteractions(config);
        assertFalse(customizerCalled);
    }

    @Test
    public void addEndpointsToConfig() {
        ServerNetworkEndpoints e1 = ServerNetworkEndpoints.localhost(1);
        ServerNetworkEndpoints e2 = ServerNetworkEndpoints.localhost(2);
        Configuration config = buildConfig(e1, e2);

        Set<TransportConfiguration> expectedAcceptors =
                Stream.of(e1, e2)
                      .map(e -> e.acceptor().transport())
                      .collect(toSet());
        Set<TransportConfiguration> actualAcceptors =
                config.getAcceptorConfigurations();
        assertThat(actualAcceptors, is(expectedAcceptors));

        Set<TransportConfiguration> expectedConnectors =
                Stream.of(e1, e2)
                        .map(e -> e.connector().transport())
                        .collect(toSet());
        Set<TransportConfiguration> actualConnectors = new HashSet<>(
            config.getConnectorConfigurations().values()
        );
        assertThat(actualConnectors, is(expectedConnectors));
    }

    @Test
    public void buildAsManyClusterConfigAsInputEndpoints() {
        ServerNetworkEndpoints e1 = ServerNetworkEndpoints.localhost(1);
        ServerNetworkEndpoints e2 = ServerNetworkEndpoints.localhost(2);

        Configuration config = buildConfig(e1);
        assertThat(config.getClusterConfigurations(), hasSize(1));

        config = buildConfig(e1, e2);
        assertThat(config.getClusterConfigurations(), hasSize(2));
    }

    @Test
    public void customizerCantOverrideLinkingOfConnector() {
        ServerNetworkEndpoints e1 = ServerNetworkEndpoints.localhost(1);
        Configuration config = buildConfig(e1);

        assertTrue(customizerCalled);

        ClusterConnectionConfiguration actual =
                config.getClusterConfigurations().get(0);
        assertThat(actual.getName(), not(isEmptyOrNullString()));  // (*)
        assertThat(actual.getConnectorName(),
                   is(e1.connector().transport().getName()));  // (*)
    }
    // (*) customizer sets "", see implementation above.

    @Test
    public void clusterConfigsHaveUniqueNames() {
        ServerNetworkEndpoints e1 = ServerNetworkEndpoints.localhost(1);
        ServerNetworkEndpoints e2 = ServerNetworkEndpoints.localhost(2);
        Configuration config = buildConfig(e1, e2);

        Set<String> names = config.getClusterConfigurations()
                                  .stream()
                                  .map(ClusterConnectionConfiguration::getName)
                                  .collect(toSet());
        assertThat(names, hasSize(2));
    }

    @Test
    public void clusterConnectionDefaultsToDeliverMessagesToAnyQueue() {
        ServerNetworkEndpoints sne = ServerNetworkEndpoints.localhost(1);
        Configuration config = buildConfig(sne);

        assertThat(config.getClusterConfigurations().get(0).getAddress(),
                   is(""));
    }

    @Test (expected = NullPointerException.class)
    public void clusterConfigThrowsIfNullCustomizer() {
        clusterConfig((Consumer<ClusterConnectionConfiguration>) null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void clusterConfigThrowsIfNullConnectors() {
        clusterConfig(c -> {}, (ServerNetworkEndpoints[]) null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void clusterConfigThrowsIfConnectorsHasNulls() {
        clusterConfig(c -> {},
                ServerNetworkEndpoints.localhost(1),
                null,
                ServerNetworkEndpoints.localhost(2));
    }

}

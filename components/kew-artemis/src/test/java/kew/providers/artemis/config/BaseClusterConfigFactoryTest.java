package kew.providers.artemis.config;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.config.ClusterConnectionConfiguration;
import org.apache.activemq.artemis.core.config.Configuration;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    private Configuration buildConfig(TransportConfiguration...connectors) {
        Configuration config = CoreConfigFactory.empty().apply(null);
        clusterConfig(this, connectors).apply(config);
        return config;
    }

    @Test
    public void doNothingIfEmptyConnectors() {
        Function<Configuration, Configuration> target = clusterConfig(this);
        Configuration config = mock(Configuration.class);
        target.apply(config);

        verifyZeroInteractions(config);
        assertFalse(customizerCalled);
    }

    @Test
    public void buildAsManyClusterConfigAsInputConnectors() {
        TransportConfiguration c1 = new NetworkTransportConfig().get();
        TransportConfiguration c2 = new NetworkTransportConfig().get();

        Configuration config = buildConfig(c1);
        assertThat(config.getClusterConfigurations(), hasSize(1));

        config = buildConfig(c1, c2);
        assertThat(config.getClusterConfigurations(), hasSize(2));
    }

    @Test
    public void filterOutDuplicatedInputConnectors() {
        TransportConfiguration c1 = new NetworkTransportConfig().get();
        TransportConfiguration c2 = new NetworkTransportConfig().get();

        Configuration config = buildConfig(c1, c2, c1, c2);
        assertThat(config.getClusterConfigurations(), hasSize(2));
    }

    @Test
    public void customizerCantOverrideLinkingOfConnector() {
        TransportConfiguration connector = new NetworkTransportConfig().get();
        Configuration config = buildConfig(connector);

        assertTrue(customizerCalled);

        ClusterConnectionConfiguration actual =
                config.getClusterConfigurations().get(0);
        assertThat(actual.getName(), not(isEmptyOrNullString()));  // (*)
        assertThat(actual.getConnectorName(), is(connector.getName()));  // (*)
    }
    // (*) customizer sets "", see implementation above.

    @Test
    public void clusterConfigsHaveUniqueNames() {
        TransportConfiguration net = new NetworkTransportConfig().get();
        TransportConfiguration inVm = new EmbeddedTransportConfig().get();
        Configuration config = buildConfig(net, inVm);

        Set<String> names = config.getClusterConfigurations()
                                  .stream()
                                  .map(ClusterConnectionConfiguration::getName)
                                  .collect(Collectors.toSet());
        assertThat(names, hasSize(2));
    }

    @Test (expected = NullPointerException.class)
    public void clusterConfigThrowsIfNullCustomizer() {
        clusterConfig((Consumer<ClusterConnectionConfiguration>) null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void clusterConfigThrowsIfNullConnectors() {
        clusterConfig(c -> {}, (TransportConfiguration[]) null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void clusterConfigThrowsIfConnectorsHasNulls() {
        clusterConfig(c -> {},
                new TransportConfiguration(),
                null,
                new TransportConfiguration());
    }

}

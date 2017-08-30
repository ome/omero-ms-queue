package kew.providers.artemis.config;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.config.ClusterConnectionConfiguration;
import org.apache.activemq.artemis.core.config.Configuration;

import org.junit.Before;
import org.junit.Test;

import java.util.function.Consumer;
import java.util.function.Function;

public class ClusterConfigFactoryTest implements ClusterConfigFactory {

    private Consumer<ClusterConnectionConfiguration> customizer;
    private TransportConfiguration[] connectors;

    @Override
    public Function<Configuration, Configuration> clusterConfig(
            Consumer<ClusterConnectionConfiguration> customizer,
            TransportConfiguration... connectors) {
        this.customizer = customizer;
        this.connectors = connectors;
        return null;
    }

    @Before
    public void setup() {
        this.customizer = null;
        this.connectors = null;
    }

    @Test
    public void buildClusterConfigWithoutCustomization() {
        clusterConfig();
        assertNotNull(customizer);
        assertNotNull(connectors);

        ClusterConnectionConfiguration conn =
                new ClusterConnectionConfiguration();
        String name = "xxxx";
        conn.setName(name);

        customizer.accept(conn);
        assertThat(conn.getName(), is(name));
    }

}

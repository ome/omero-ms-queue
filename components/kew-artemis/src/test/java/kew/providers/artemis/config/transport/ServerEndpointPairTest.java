package kew.providers.artemis.config.transport;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import kew.providers.artemis.config.CoreConfigFactory;
import org.apache.activemq.artemis.core.config.Configuration;
import org.junit.Before;
import org.junit.Test;

public class ServerEndpointPairTest implements ServerEndpointPair {

    private AcceptorConfig acceptor;
    private ConnectorConfig connector;

    @Before
    public void setup() {
        acceptor = new NetworkAcceptorConfig();
        connector = new NetworkConnectorConfig();
    }

    @Override
    public AcceptorConfig acceptor() {
        return acceptor;
    }

    @Override
    public ConnectorConfig connector() {
        return connector;
    }

    @Test
    public void configureConnectorAcceptorPair() {
        Configuration actual = CoreConfigFactory.empty()
                                                .with(this::transportConfig)
                                                .apply(null);

        assertThat(actual.getAcceptorConfigurations().size(), is(1));
        assertTrue(actual.getAcceptorConfigurations()
                         .contains(acceptor().transport()));
        assertThat(actual.getConnectorConfigurations().size(), is(1));
        assertThat(actual.getConnectorConfigurations().values().toArray()[0],
                   is(connector().transport()));
    }

    @Test (expected = NullPointerException.class)
    public void transportConfigThrowsIfNullConfig() {
        transportConfig(null);
    }

}

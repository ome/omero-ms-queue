package kew.providers.artemis.config;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.remoting.impl.invm.TransportConstants;
import org.junit.Test;

import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;


public class EmbeddedServerTransportConfigTest {

    private static int getServerId(TransportConfiguration tc) {
        return (int) tc.getParams()
                       .get(TransportConstants.SERVER_ID_PROP_NAME);
    }


    @Test
    public void generateSequentialServerIds() {
        int[] expected = new int[] { 1, 2, 3 };

        IntUnaryOperator f = x -> new EmbeddedServerTransportConfig()
                                        .embeddedServerId();
        int[] actual = IntStream.of(expected).map(f).toArray();

        assertArrayEquals(expected, actual);
    }

    @Test
    public void connectorAndAcceptorHaveSameServerId() {
        EmbeddedServerTransportConfig cfg = new EmbeddedServerTransportConfig();
        int serverId = cfg.embeddedServerId();
        int acceptorId = getServerId(cfg.embeddedAcceptor());
        int connectorId = getServerId(cfg.embeddedConnector());

        assertThat(serverId, is(acceptorId));
        assertThat(serverId, is(connectorId));
    }

    @Test
    public void configureConnectorAcceptorPair() {
        EmbeddedServerTransportConfig tc = new EmbeddedServerTransportConfig();
        Configuration actual = CoreConfigFactory.empty()
                                                .with(tc::embeddedTransport)
                                                .apply(null);

        assertThat(actual.getAcceptorConfigurations().size(), is(1));
        assertTrue(
                actual.getAcceptorConfigurations().contains(
                        tc.embeddedAcceptor()
                ));
        assertThat(actual.getConnectorConfigurations().size(), is(1));
        assertThat(actual.getConnectorConfigurations().values().toArray()[0],
                   is(tc.embeddedConnector()));
    }

}

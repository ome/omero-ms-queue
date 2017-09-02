package kew.providers.artemis.config.transport;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import kew.providers.artemis.config.CoreConfigFactory;
import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.remoting.impl.invm.TransportConstants;
import org.junit.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ServerEmbeddedEndpointsTest {

    private static int getServerId(TransportConfiguration tc) {
        return (int) tc.getParams()
                       .get(TransportConstants.SERVER_ID_PROP_NAME);
    }


    @Test
    public void generateUniqueServerIds() {
        Set<Integer> actual = Stream.of(1, 2, 3)
                                    .map(x -> new ServerEmbeddedEndpoints()
                                             .embeddedServerId())
                                    .collect(Collectors.toSet());
        assertThat(actual, hasSize(3));
    }

    @Test
    public void connectorAndAcceptorHaveSameServerId() {
        ServerEmbeddedEndpoints cfg = new ServerEmbeddedEndpoints();
        int serverId = cfg.embeddedServerId();
        int acceptorId = getServerId(cfg.acceptor().transport());
        int connectorId = getServerId(cfg.connector().transport());

        assertThat(serverId, is(acceptorId));
        assertThat(serverId, is(connectorId));
    }

    @Test
    public void configureConnectorAcceptorPair() {
        ServerEmbeddedEndpoints tc = new ServerEmbeddedEndpoints();
        Configuration actual = CoreConfigFactory.empty()
                                                .with(tc::transportConfig)
                                                .apply(null);

        assertThat(actual.getAcceptorConfigurations().size(), is(1));
        assertTrue(
                actual.getAcceptorConfigurations().contains(
                        tc.acceptor().transport()
                ));
        assertThat(actual.getConnectorConfigurations().size(), is(1));
        assertThat(actual.getConnectorConfigurations().values().toArray()[0],
                   is(tc.connector().transport()));
    }

}

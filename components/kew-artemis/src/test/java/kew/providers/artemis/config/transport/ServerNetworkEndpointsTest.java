package kew.providers.artemis.config.transport;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.remoting.impl.netty.TransportConstants;
import org.junit.Test;
import util.object.Builder;
import util.types.PositiveN;

public class ServerNetworkEndpointsTest {

    private static String getHost(TransportConfiguration tc) {
        return (String) tc.getParams()
                          .get(TransportConstants.HOST_PROP_NAME);
    }

    private static int getPort(TransportConfiguration tc) {
        return (int) tc.getParams()
                       .get(TransportConstants.PORT_PROP_NAME);
    }

    @Test
    public void connectorAndAcceptorHaveSameHost() {
        String host = "10.10.0.11";
        ServerNetworkEndpoints cfg =
                new ServerNetworkEndpoints(host, PositiveN.of(1234));

        String acceptorHost = getHost(cfg.acceptor().transport());
        String connectorHost = getHost(cfg.connector().transport());

        assertThat(cfg.ipOrHostName(), is(host));
        assertThat(acceptorHost, is(host));
        assertThat(connectorHost, is(host));
    }

    @Test
    public void connectorAndAcceptorHaveSamePort() {
        int port = 1234;
        ServerNetworkEndpoints cfg =
                new ServerNetworkEndpoints("youhost", PositiveN.of(port));

        int acceptorPort = getPort(cfg.acceptor().transport());
        int connectorPort = getPort(cfg.connector().transport());

        assertThat(cfg.portNumber(), is(port));
        assertThat(acceptorPort, is(port));
        assertThat(connectorPort, is(port));
    }

    @Test
    public void accidentallyOverrideHost() {
        String initialHost = "youhost";
        String override = "****";
        ServerNetworkEndpoints cfg =
                new ServerNetworkEndpoints(initialHost, PositiveN.of(1));
        Builder.make(cfg::acceptor)
               .with(NetworkTransportProps.host(override))
               .apply(null);

        String acceptorHost = getHost(cfg.acceptor().transport());
        String connectorHost = getHost(cfg.connector().transport());

        assertThat(cfg.ipOrHostName(), is(initialHost));
        assertThat(acceptorHost, is(override));
        assertThat(connectorHost, is(initialHost));
    }
    /* NOTE. Mutability.
     * This test is just a reminder that we're not yet enforcing invariants
     * at the type-level. (See note about it in ServerNetworkEndpoints.)
     * The test shows that it's possible to get the ServerNetworkEndpoints
     * in an inconsistent state.
     */

    @Test
    public void accidentallyOverridePort() {
        int initialPort = 1234;
        int override = 4321;
        ServerNetworkEndpoints cfg =
                new ServerNetworkEndpoints("host", PositiveN.of(initialPort));
        Builder.make(cfg::connector)
                .with(NetworkTransportProps.port(PositiveN.of(override)))
                .apply(null);

        int acceptorPort = getPort(cfg.acceptor().transport());
        int connectorPort = getPort(cfg.connector().transport());

        assertThat(cfg.portNumber(), is(initialPort));
        assertThat(acceptorPort, is(initialPort));
        assertThat(connectorPort, is(override));
    }
    /* NOTE. Mutability.
     * This test is just a reminder that we're not yet enforcing invariants
     * at the type-level. (See note about it in ServerNetworkEndpoints.)
     * The test shows that it's possible to get the ServerNetworkEndpoints
     * in an inconsistent state.
     */

    @Test (expected = IllegalArgumentException.class)
    public void ctorThrowsIfNullHost() {
        new ServerNetworkEndpoints(null, PositiveN.of(1));
    }

    @Test (expected = IllegalArgumentException.class)
    public void ctorThrowsIfEmptyHost() {
        new ServerNetworkEndpoints("", PositiveN.of(1));
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullPort() {
        new ServerNetworkEndpoints("xxx", null);
    }

}

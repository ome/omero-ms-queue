package kew.providers.artemis.config.transport;

import static kew.providers.artemis.config.transport.NetworkTransportProps.host;
import static kew.providers.artemis.config.transport.NetworkTransportProps.port;

import java.util.function.Supplier;

import util.object.Builder;
import util.types.PositiveN;

/**
 * Holds the configuration of an Artemis network acceptor/connector pair.
 * The acceptor and connector configurations are created with the same host
 * and port number.
 */
public class ServerNetworkEndpoints implements ServerEndpointPair {

    /**
     * Factory method to instantiate an acceptor/connector pair bound to
     * "localhost" on the specified port.
     * @param port the port number for the acceptor/connector pair.
     * @return the acceptor/connector pair.
     * @throws IllegalArgumentException if the port isn't positive.
     */
    public static ServerNetworkEndpoints localhost(int port) {
        return new ServerNetworkEndpoints("localhost", PositiveN.of(port));
    }


    private final NetworkAcceptorConfig networkAcceptor;
    private final NetworkConnectorConfig networkConnector;
    private final String ipOrHostName;
    private final int portNumber;

    /**
     * Creates a new instance.
     * @param ipOrHostName the IP or host name for the acceptor/connector pair.
     * @param port the port number for the acceptor/connector pair.
     * @throws IllegalArgumentException if the host is {@code null} or empty.
     * @throws NullPointerException if the port is {@code null}.
     */
    public ServerNetworkEndpoints(String ipOrHostName, PositiveN port) {
        networkAcceptor = makeTransport(NetworkAcceptorConfig::new,
                                        ipOrHostName, port);
        networkConnector = makeTransport(NetworkConnectorConfig::new,
                                         ipOrHostName, port);
        this.ipOrHostName = ipOrHostName;
        this.portNumber = port.get().intValue();
    }

    private <T extends EndpointConfig & HasNetworkProps>
    T makeTransport(Supplier<T> ctor, String h, PositiveN p) {
        return Builder.make(ctor)
                      .with(host(h))
                      .with(port(p))
                      .apply(null);
    }
    /* NOTE. Customisation.
     * To add more props, get the acceptor/connector and use a Builder, e.g.
     *
     *     sne = new ServerNetworkEndpoints ...
     *     Builder.make(sne::connector).with(someProp).with(someOtherProp)
     */

    @Override
    public NetworkAcceptorConfig acceptor() {
        return networkAcceptor;
    }

    @Override
    public NetworkConnectorConfig connector() {
        return networkConnector;
    }

    /**
     * @return the IP or host name shared by both acceptor and connector.
     */
    public String ipOrHostName() {
        return ipOrHostName;
    }

    /**
     * @return the port shared by both acceptor and connector.
     */
    public int portNumber() {
        return portNumber;
    }

}
/* NOTE. Mutability.
 * Ideally we'd like to maintain these invariants:
 * - the acceptor/connector name is unique and never changes.
 * - both acceptor's host and port = connector's and never change.
 * But you can always get hold of the underlying transport and shoot yourself
 * in the foot cos it's a mutable object.
 * We could try to enforce these
 * invariants at the type-level but it's just too much work, so will rely on
 * clients behaving themselves and never change the name, host, or port of the
 * underlying transport...
 */
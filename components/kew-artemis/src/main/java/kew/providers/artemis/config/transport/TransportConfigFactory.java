package kew.providers.artemis.config.transport;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.remoting.impl.invm.InVMAcceptorFactory;
import org.apache.activemq.artemis.core.remoting.impl.invm.InVMConnectorFactory;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyAcceptorFactory;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory;

import util.types.UuidString;

/**
 * Factory methods to create transport configurations.
 */
public class TransportConfigFactory {

    /**
     * Factory method to create a transport configuration suitable for a
     * certain type of endpoint.
     * The transport is created with a unique name and an empty parameters map.
     * @param endpointFactory one of the Artemis endpoint factory classes;
     *                        determines the kind of endpoint this transport
     *                        is for, i.e. one of: in-vm acceptor, in-vm
     *                        connector, netty acceptor, netty connector.
     * @return the transport configuration object.
     * @throws NullPointerException if the argument is {@code null}.
     */
    private static TransportConfiguration makeTransport(
            Class<?> endpointFactory) {
        requireNonNull(endpointFactory, "endpointFactory");

        return new TransportConfiguration(endpointFactory.getCanonicalName(),
                new HashMap<>(),
                new UuidString().id());
    }

    /**
     * Factory method to create a transport configuration suitable for an
     * in-vm acceptor.
     * The transport is created with a unique name and an empty parameters map.
     * @return the transport configuration object.
     */
    public static TransportConfiguration makeEmbeddedAcceptorTransport() {
        return makeTransport(InVMAcceptorFactory.class);
    }

    /**
     * Factory method to create a transport configuration suitable for an
     * in-vm connector.
     * The transport is created with a unique name and an empty parameters map.
     * @return the transport configuration object.
     */
    public static TransportConfiguration makeEmbeddedConnectorTransport() {
        return makeTransport(InVMConnectorFactory.class);
    }

    /**
     * Factory method to create a transport configuration suitable for a
     * network acceptor.
     * The transport is created with a unique name and an empty parameters map.
     * @return the transport configuration object.
     */
    public static TransportConfiguration makeNetworkAcceptorTransport() {
        return makeTransport(NettyAcceptorFactory.class);
    }

    /**
     * Factory method to create a transport configuration suitable for a
     * network connector.
     * The transport is created with a unique name and an empty parameters map.
     * @return the transport configuration object.
     */
    public static TransportConfiguration makeNetworkConnectorTransport() {
        return makeTransport(NettyConnectorFactory.class);
    }

}

package kew.providers.artemis.config.transport;

import static java.util.Objects.requireNonNull;
import static util.string.Strings.requireString;

import java.util.function.Function;

import org.apache.activemq.artemis.core.remoting.impl.netty.TransportConstants;

import util.types.PositiveN;

/**
 * Type-safe configuration properties for the parameters of an underlying
 * Artemis network configuration.
 */
public class NetworkTransportProps {

    private static <T extends EndpointConfig & HasNetworkProps, V>
    Function<T, T>
            connectionParam(String key, V value) {
        requireNonNull(key, "key");
        requireNonNull(value, "value");

        return endpoint -> {
            endpoint.params().put(key, value);
            return endpoint;
        };
    }
    /* NOTE. If you need to set properties other than those below, don't take
     * the shortcut and make this method public. Rather add the type-safe
     * property's counterpart as done below.
     */

    /**
     * A setter to specify the host of the server to connect to.
     * @param nameOrIp the server's host name or IP address.
     * @return the setter.
     */
    public static <T extends EndpointConfig & HasNetworkProps>
    Function<T, T> host(String nameOrIp) {
        requireString(nameOrIp, "nameOrIp");

        return connectionParam(TransportConstants.HOST_PROP_NAME, nameOrIp);
    }

    /**
     * A setter to specify the port number of the server to connect to.
     * @param number the server's port number.
     * @return the setter.
     */
    public static <T extends EndpointConfig & HasNetworkProps>
    Function<T, T> port(PositiveN number) {
        requireNonNull(number, "number");

        return connectionParam(TransportConstants.PORT_PROP_NAME,
                               number.get().intValue());
    }

}
/* NOTE. Type-Safety.
 * Netty acceptors and connectors share most properties but not all. Some props
 * are only applicable to acceptors whereas others only to connectors. Look at
 * TransportConstants.ALLOWABLE_ACCEPTOR_KEYS/ALLOWABLE_CONNECTOR_KEYS for the
 * details.
 * To avoid setting a connector only property on an acceptor (or vice versa),
 * we use different types to distinguish the three possible cases:
 *
 * + <T extends EndpointConfig & HasNetworkProps> Function<T, T> propName(...)
 *   for properties that can be set both on acceptors and connectors
 *
 * + <T extends AcceptorConfig & HasNetworkProps> propName(...)
 *   for properties that are only applicable to acceptors
 *
 * + <T extends ConnectorConfig & HasNetworkProps> propName(...)
 *   for properties that are only applicable to connectors
 *
 */
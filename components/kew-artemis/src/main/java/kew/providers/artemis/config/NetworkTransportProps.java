package kew.providers.artemis.config;

import static java.util.Objects.requireNonNull;
import static util.string.Strings.requireString;

import java.util.function.Consumer;

import org.apache.activemq.artemis.core.remoting.impl.netty.TransportConstants;

import util.types.PositiveN;

/**
 * Type-safe configuration properties for the connection parameters of an
 * underlying Artemis network configuration.
 */
public class NetworkTransportProps {

    private static <T> Consumer<NetworkTransportConfig>
            connectionParam(String key, T value) {
        requireNonNull(key, "key");
        requireNonNull(value, "value");

        return ntc -> ntc.params().put(key, value);
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
    public static Consumer<NetworkTransportConfig> host(String nameOrIp) {
        requireString(nameOrIp, "nameOrIp");

        return connectionParam(TransportConstants.HOST_PROP_NAME, nameOrIp);
    }

    /**
     * A setter to specify the port number of the server to connect to.
     * @param number the server's port number.
     * @return the setter.
     */
    public static Consumer<NetworkTransportConfig> port(PositiveN number) {
        requireNonNull(number, "number");

        return connectionParam(TransportConstants.PORT_PROP_NAME,
                               number.get().intValue());
    }

}

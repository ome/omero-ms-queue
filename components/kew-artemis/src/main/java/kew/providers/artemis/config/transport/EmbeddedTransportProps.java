package kew.providers.artemis.config.transport;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

import org.apache.activemq.artemis.core.remoting.impl.invm.TransportConstants;

import util.types.PositiveN;

/**
 * Type-safe configuration properties for the connection parameters of an
 * underlying Artemis embedded transport configuration.
 */
public class EmbeddedTransportProps {

    private static <T extends EndpointConfig & HasEmbeddedProps, V>
    Function<T, T> connectionParam(String key, V value) {
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
     * A setter to specify the ID of the server to connect to.
     * @param id the server ID.
     * @return the setter.
     */
    public static <T extends EndpointConfig & HasEmbeddedProps>
    Function<T, T> serverId(PositiveN id) {
        requireNonNull(id, "id");

        return connectionParam(TransportConstants.SERVER_ID_PROP_NAME,
                               id.get().intValue());
    }

}

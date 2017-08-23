package kew.providers.artemis.config;

import static java.util.Objects.requireNonNull;

import org.apache.activemq.artemis.core.remoting.impl.invm.TransportConstants;
import util.types.PositiveN;

import java.util.function.Consumer;

/**
 * Type-safe configuration properties for the connection parameters of an
 * underlying Artemis embedded transport configuration.
 */
public class EmbeddedTransportProps {

    private static <T> Consumer<EmbeddedTransportConfig>
        connectionParam(String key, T value) {
        requireNonNull(key, "key");
        requireNonNull(value, "value");

        return etc -> etc.params().put(key, value);
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
    public static Consumer<EmbeddedTransportConfig> serverId(PositiveN id) {
        requireNonNull(id, "id");

        return connectionParam(TransportConstants.SERVER_ID_PROP_NAME,
                               id.get().intValue());
    }

}

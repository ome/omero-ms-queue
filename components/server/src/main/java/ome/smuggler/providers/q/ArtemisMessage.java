package ome.smuggler.providers.q;

import static java.util.Objects.requireNonNull;

import org.apache.activemq.artemis.api.core.client.ClientMessage;

/**
 * Adapter to make Artemis {@link ClientMessage} functionality available
 * through the various {@code Has*} service provider interfaces of the
 * queue channel.
 */
public class ArtemisMessage {

    private final ClientMessage adaptee;

    /**
     * Creates a new instance.
     * @param adaptee the underlying queue message that provides the
     *                actual functionality.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public ArtemisMessage(ClientMessage adaptee) {
        requireNonNull(adaptee, "adaptee");
        this.adaptee = adaptee;
    }

    /**
     * @return the underlying queue message.
     */
    public ClientMessage message() {
        return adaptee;
    }

}

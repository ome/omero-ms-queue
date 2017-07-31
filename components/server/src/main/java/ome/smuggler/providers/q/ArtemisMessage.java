package ome.smuggler.providers.q;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.activemq.artemis.api.core.Message;
import org.apache.activemq.artemis.api.core.client.ClientMessage;

import kew.core.qchan.spi.HasProps;
import kew.core.qchan.spi.HasReceiptAck;
import kew.core.qchan.spi.HasSchedule;
import util.types.FutureTimepoint;


/**
 * Adapter to make Artemis {@link ClientMessage} functionality available
 * through the various {@code Has*} service provider interfaces of the
 * queue channel.
 */
public class ArtemisMessage implements HasProps, HasReceiptAck, HasSchedule {

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

    private <T> Optional<T> getProp(String key, Function<String, T> getter) {
        requireNonNull(key, "key");
        requireNonNull(getter, "getter");

        if (adaptee.containsProperty(key)) {
            return Optional.ofNullable(getter.apply(key));
        }
        return Optional.empty();
    }

    private <T> void putProp(String key, T value,
                             BiFunction<String, T, ClientMessage> setter) {
        requireNonNull(key, "key");
        requireNonNull(value, "value");
        requireNonNull(setter, "setter");

        setter.apply(key, value);
    }

    /**
     * @return the underlying queue message.
     */
    public ClientMessage message() {
        return adaptee;
    }

    @Override
    public void setScheduledDeliveryTime(FutureTimepoint when) {
        putProp(Message.HDR_SCHEDULED_DELIVERY_TIME.toString(),
                when.get().toMillis());
    }

    @Override
    public void putProp(String key, String value) {
        putProp(key, value, adaptee::putStringProperty);
    }

    @Override
    public void putProp(String key, long value) {
        putProp(key, value, adaptee::putLongProperty);
    }

    @Override
    public Optional<String> lookupStringValue(String key) {
        return getProp(key, adaptee::getStringProperty);
    }

    @Override
    public Optional<Long> lookupLongValue(String key) {
        return getProp(key, adaptee::getLongProperty);
    }

    @Override
    public void removeFromQueue() throws Exception {
        adaptee.acknowledge();
    }

}

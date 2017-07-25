package ome.smuggler.providers.q;

import static java.util.Objects.requireNonNull;
import static util.string.Strings.requireString;

import java.util.Optional;
import java.util.function.Function;

import org.apache.activemq.artemis.api.core.Message;
import org.apache.activemq.artemis.api.core.client.ClientMessage;

import kew.core.msg.CountedSchedule;
import util.types.FutureTimepoint;
import util.types.PositiveN;

public class Messages {

    public static final String ScheduleCountKey = 
            CountedSchedule.class.getName() + "#count";

    public static Function<QueueConnector, ClientMessage> durableMessage() {
        return QueueConnector::newDurableMessage;
    }
    
    public static Function<ClientMessage, ClientMessage> setProp(
            String key, long value) {
        requireString(key, "key");
        return m -> (ClientMessage) m.putLongProperty(key, value);
    }

    public static Function<ClientMessage, ClientMessage> setScheduledDeliveryTime(
            FutureTimepoint when) {
        return setProp(Message.HDR_SCHEDULED_DELIVERY_TIME.toString(), 
                       when.get().toMillis());
    }
    
    public static Function<ClientMessage, ClientMessage> setScheduleCount(
            PositiveN count) {
        return setProp(ScheduleCountKey, count.get());
    }
    
    private static <T> Optional<T> getProp(String key, ClientMessage msg,
                                           Function<String, T> getter) {
        requireNonNull(key, "key");
        requireNonNull(msg, "msg");
        requireNonNull(getter, "getter");
        
        if (msg.containsProperty(key)) {
            return Optional.ofNullable(getter.apply(key));
        }
        return Optional.empty();
    }
    
    public static Optional<PositiveN> getScheduleCount(ClientMessage msg) {
        return getProp(ScheduleCountKey, msg, msg::getLongProperty)
              .map(PositiveN::of);
    }
    
}

package kew.core.msg;

/**
 * Plugs into a channel sink to consume its data and optionally request that 
 * the same data be delivered again.
 * Use in conjunction with {@link MessageRepeater} or {@link OnceOffSchedule}
 * and {@link OnceOffRepeatConsumer}.
 */
public interface RepeatConsumer<T> {
    
    /**
     * Consumes the message data and returns an indication of whether the same
     * data should be delivered again.
     * @param data the data received from the channel.
     * @return either {@link RepeatAction#Repeat Repeat} to indicate the data
     * should be delivered again or {@link RepeatAction#Stop Stop} to indicate
     * no further action should be taken.
     * @throws NullPointerException if the argument is {@code null}.
     */
    RepeatAction consume(T data);
    
}

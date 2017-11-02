package kew.core.qchan;

import static java.util.Objects.requireNonNull;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

import kew.core.msg.*;
import kew.core.qchan.spi.HasProps;
import kew.core.qchan.spi.HasReceiptAck;
import kew.core.qchan.spi.HasSchedule;
import util.io.SinkWriter;
import util.io.SourceReader;

/**
 * Factory methods to create messaging channels backed by message queues
 * or any other middleware with similar capabilities.
 * This is just a convenience class that wraps a {@link QChannelFactory} to
 * offer exactly the same methods but with fixed serializers and deserializers
 * arguments.
 * @param <QM> the message type in the underlying middleware.
 * @param <T> the type of the message data.
 */
public class QChannelFactoryAdapter
        <QM extends HasReceiptAck & HasSchedule & HasProps, T> {

    private final QChannelFactory<QM, T> factory;
    private final SinkWriter<T, OutputStream> serializer;
    private final SourceReader<InputStream, T> deserializer;

    /**
     * Creates a new instance.
     * @param factory the underlying factory providing the actual functionality.
     * @param serializer serialises the message data, a {@code T}-value.
     * @param deserializer de-serialises the message data, a {@code T}-value.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public QChannelFactoryAdapter(QChannelFactory<QM, T> factory,
                                  SinkWriter<T, OutputStream> serializer,
                                  SourceReader<InputStream, T> deserializer) {
        requireNonNull(factory, "factory");
        requireNonNull(serializer, "serializer");
        requireNonNull(deserializer, "deserializer");

        this.factory = factory;
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    /**
     * Creates a new channel source to send durable messages.
     * @return the channel source.
     * @throws Exception if this source couldn't be connected to the underlying
     * queue.
     */
    public ChannelSource<T> buildSource() throws Exception {
        return factory.buildSource(serializer);
    }

    /**
     * Creates a new channel source to send durable messages to be delivered
     * at specified times.
     * @return the channel source.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws Exception if this source couldn't be connected to the underlying
     * queue.
     */
    public SchedulingSource<T> buildSchedulingSource() throws Exception {
        return factory.buildSchedulingSource(serializer);
    }

    /**
     * Creates a new channel source to send durable messages to be delivered
     * at specified times with an additional message count.
     * @return the channel source.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws Exception if this source couldn't be connected to the underlying
     * queue.
     */
    public MessageSource<CountedSchedule, T> buildCountedScheduleSource()
            throws Exception {
        return factory.buildCountedScheduleSource(serializer);
    }

    /**
     * Creates a new channel sink to receive messages from the underlying
     * queue.
     * If the process terminates abnormally (e.g. segfault, power failure)
     * while the consumer is busy processing a message, the message will be
     * delivered again once the process is rebooted.
     * @param consumer consumes the message data.
     * @return the channel sink.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws Exception if this sink couldn't be connected to the underlying
     * queue.
     */
    public MessageSink<QM, InputStream> buildSink(ChannelSink<T> consumer)
            throws Exception {
        return factory.buildSink(consumer, deserializer);
    }

    /**
     * Creates a new channel sink to receive messages from the underlying
     * queue.
     * @param consumer consumes the message data.
     * @param redeliverOnRecovery if {@code true} and the process terminates
     * abnormally (e.g. segfault, power failure) while the consumer is busy
     * processing a message, the message will be delivered again once the
     * process is rebooted. If {@code false}, a message will only ever be
     * delivered once to the consumer.
     * @return the channel sink.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws Exception if this sink couldn't be connected to the underlying
     * queue.
     */
    public MessageSink<QM, InputStream> buildSink(
            ChannelSink<T> consumer,
            boolean redeliverOnRecovery)
                throws Exception {
        return factory.buildSink(consumer, deserializer, redeliverOnRecovery);
    }

    /**
     * Creates a new channel sink to receive messages from the underlying
     * queue that were sent with {@link CountedSchedule} metadata.
     * If the process terminates abnormally (e.g. segfault, power failure)
     * while the consumer is busy processing a message, the message will be
     * delivered again once the process is rebooted.
     * @param consumer consumes the message.
     * @return the channel sink.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws Exception if this sink couldn't be connected to the underlying
     * queue.
     */
    public MessageSink<QM, InputStream> buildCountedScheduleSink(
            MessageSink<CountedSchedule, T> consumer)
              throws Exception {
        return factory.buildCountedScheduleSink(consumer, deserializer);
    }

    /**
     * Creates a new channel sink to receive messages from the underlying
     * queue that were sent with {@link CountedSchedule} metadata.
     * @param consumer consumes the message.
     * @param redeliverOnRecovery if {@code true} and the process terminates
     * abnormally (e.g. segfault, power failure) while the consumer is busy
     * processing a message, the message will be delivered again once the
     * process is rebooted. If {@code false}, a message will only ever be
     * delivered once to the consumer.
     * @return the channel sink.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws Exception if this sink couldn't be connected to the underlying
     * queue.
     */
    public MessageSink<QM, InputStream> buildCountedScheduleSink(
            MessageSink<CountedSchedule, T> consumer,
            boolean redeliverOnRecovery)
                throws Exception {
        return factory.buildCountedScheduleSink(consumer,
                                                deserializer,
                                                redeliverOnRecovery);
    }

    /**
     * Creates a new channel sink to receive messages from the underlying
     * queue, allowing the consumer to send more messages.
     * If the process terminates abnormally (e.g. segfault, power failure)
     * while the consumer is busy processing a message, the message will be
     * delivered again once the process is rebooted.
     * @param consumer consumes the message and optionally sends a new one.
     * @return the channel sink.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws Exception if this sink couldn't be connected to the underlying
     * queue.
     */
    public MessageSink<QM, InputStream> buildReschedulableSink(
            Reschedulable<T> consumer)
                throws Exception {
        return factory.buildReschedulableSink(consumer,
                                              serializer,
                                              deserializer);
    }

    /**
     * Creates a new channel sink to receive messages from the underlying
     * queue, allowing the consumer to send more messages.
     * @param consumer consumes the message and optionally sends a new one.
     * @param redeliverOnRecovery if {@code true} and the process terminates
     * abnormally (e.g. segfault, power failure) while the consumer is busy
     * processing a message, the message will be delivered again once the
     * process is rebooted. If {@code false}, a message will only ever be
     * delivered once to the consumer.
     * @return the channel sink.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws Exception if this sink couldn't be connected to the underlying
     * queue.
     */
    public MessageSink<QM, InputStream> buildReschedulableSink(
            Reschedulable<T> consumer,
            boolean redeliverOnRecovery)
                throws Exception {
        return factory.buildReschedulableSink(consumer,
                                              serializer,
                                              deserializer,
                                              redeliverOnRecovery);
    }

    /**
     * Builds a message sink to receive messages and pass them on as {@code
     * T}-values to the specified repeat consumer.
     * If the process terminates abnormally (e.g. segfault, power failure)
     * while the consumer is busy processing a message, the message will be
     * delivered again once the process is rebooted.
     * @param processor consumes data received from the queue.
     * @param retryIntervals intervals at which to re-deliver a message the
     *                       processor failed to consume successfully.
     * @param failureHandler handles a message the processor wasn't able to
     *                       consume successfully after trying for the number
     *                       of times specified by the retry intervals.
     * @return the message sink.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws Exception if this sink couldn't be connected to the underlying
     * queue.
     */
    public MessageSink<QM, InputStream> buildRepeatSink(
            RepeatConsumer<T> processor,
            List<Duration> retryIntervals,
            Consumer<T> failureHandler)
                throws Exception {
        return factory.buildRepeatSink(processor,
                                       retryIntervals,
                                       failureHandler,
                                       serializer,
                                       deserializer);
    }

}

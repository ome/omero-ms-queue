package kew.core.qchan;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

import kew.core.msg.*;
import kew.core.qchan.impl.*;
import kew.core.qchan.spi.*;
import util.io.SinkWriter;
import util.io.SourceReader;

/**
 * Factory methods to create messaging channels backed by message queues
 * or any other middleware with similar capabilities.
 * @param <QM> the message type in the underlying middleware.
 * @param <T> the type of the message data.
 */
public interface QChannelFactory
        <QM extends HasReceiptAck & HasSchedule & HasProps, T> {

    /**
     * Provides the means to access a queue in the underlying middleware
     * so that messages can be sent and received on that queue.
     * @return a queue connector.
     */
    QConnector<QM> queue();

    /**
     * Creates a new channel source to send durable messages.
     * @param serializer serialises the message data, a {@code T}-value.
     * @return the channel source.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws Exception if this source couldn't be connected to the underlying
     * queue.
     */
    default ChannelSource<T> buildSource(
            SinkWriter<T, OutputStream> serializer)
            throws Exception {
        return new EnqueueTask<>(queue().newProducer(), serializer)
                .asDataSource();
    }

    /**
     * Creates a new channel source to send durable messages to be delivered
     * at specified times.
     * @param serializer serialises the message data, a {@code T}-value.
     * @return the channel source.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws Exception if this source couldn't be connected to the underlying
     * queue.
     */
    default SchedulingSource<T> buildSchedulingSource(
            SinkWriter<T, OutputStream> serializer)
            throws Exception {
        return new ScheduleTask<>(queue().newProducer(), serializer);
    }

    /**
     * Creates a new channel source to send durable messages to be delivered
     * at specified times with an additional message count.
     * @param serializer serialises the message data, a {@code T}-value.
     * @return the channel source.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws Exception if this source couldn't be connected to the underlying
     * queue.
     */
    default MessageSource<CountedSchedule, T> buildCountedScheduleSource(
            SinkWriter<T, OutputStream> serializer)
            throws Exception {
        return new CountedScheduleTask<>(queue().newProducer(), serializer);
    }

    /**
     * Creates a new channel sink to receive messages from the underlying
     * queue.
     * If the process terminates abnormally (e.g. segfault, power failure)
     * while the consumer is busy processing a message, the message will be
     * delivered again once the process is rebooted.
     * @param consumer consumes the message data.
     * @param deserializer de-serialises the message data, a {@code T}-value.
     * @return the channel sink.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws Exception if this sink couldn't be connected to the underlying
     * queue.
     */
    default MessageSink<QM, InputStream> buildSink(
            ChannelSink<T> consumer,
            SourceReader<InputStream, T> deserializer)
            throws Exception {
        return buildSink(consumer, deserializer, true);
    }

    /**
     * Creates a new channel sink to receive messages from the underlying
     * queue.
     * @param consumer consumes the message data.
     * @param deserializer de-serialises the message data, a {@code T}-value.
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
    default MessageSink<QM, InputStream> buildSink(
            ChannelSink<T> consumer,
            SourceReader<InputStream, T> deserializer,
            boolean redeliverOnRecovery)
            throws Exception {
        return new DequeueTask<>(queue(), consumer, deserializer,
                                 redeliverOnRecovery);
    }

    /**
     * Creates a new channel sink to receive messages from the underlying
     * queue that were sent with {@link CountedSchedule} metadata.
     * If the process terminates abnormally (e.g. segfault, power failure)
     * while the consumer is busy processing a message, the message will be
     * delivered again once the process is rebooted.
     * @param consumer consumes the message.
     * @param deserializer de-serialises the message data, a {@code T}-value.
     * @return the channel sink.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws Exception if this sink couldn't be connected to the underlying
     * queue.
     */
    default MessageSink<QM, InputStream> buildCountedScheduleSink(
            MessageSink<CountedSchedule, T> consumer,
            SourceReader<InputStream, T> deserializer)
            throws Exception {
        return buildCountedScheduleSink(consumer, deserializer, true);
    }

    /**
     * Creates a new channel sink to receive messages from the underlying
     * queue that were sent with {@link CountedSchedule} metadata.
     * @param consumer consumes the message.
     * @param deserializer de-serialises the message data, a {@code T}-value.
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
    default MessageSink<QM, InputStream> buildCountedScheduleSink(
            MessageSink<CountedSchedule, T> consumer,
            SourceReader<InputStream, T> deserializer,
            boolean redeliverOnRecovery)
            throws Exception {
        CountedScheduleSink<QM, T> sink =
                new CountedScheduleSink<>(consumer);
        return new DequeueTask<>(queue(), sink, deserializer, redeliverOnRecovery);
    }

    /**
     * Creates a new channel sink to receive messages from the underlying
     * queue, allowing the consumer to send more messages.
     * If the process terminates abnormally (e.g. segfault, power failure)
     * while the consumer is busy processing a message, the message will be
     * delivered again once the process is rebooted.
     * @param consumer consumes the message and optionally sends a new one.
     * @param serializer serialises the message data, a {@code T}-value.
     * @param deserializer de-serialises the message data, a {@code T}-value.
     * @return the channel sink.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws Exception if this sink couldn't be connected to the underlying
     * queue.
     */
    default MessageSink<QM, InputStream> buildReschedulableSink(
            Reschedulable<T> consumer,
            SinkWriter<T, OutputStream> serializer,
            SourceReader<InputStream, T> deserializer)
            throws Exception {
        return buildReschedulableSink(consumer, serializer, deserializer, true);
    }

    /**
     * Creates a new channel sink to receive messages from the underlying
     * queue, allowing the consumer to send more messages.
     * @param consumer consumes the message and optionally sends a new one.
     * @param serializer serialises the message data, a {@code T}-value.
     * @param deserializer de-serialises the message data, a {@code T}-value.
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
    default MessageSink<QM, InputStream> buildReschedulableSink(
            Reschedulable<T> consumer,
            SinkWriter<T, OutputStream> serializer,
            SourceReader<InputStream, T> deserializer,
            boolean redeliverOnRecovery)
            throws Exception {
        MessageSource<CountedSchedule, T> loopback =
                buildCountedScheduleSource(serializer);
        ReschedulingSink<T> sink = new ReschedulingSink<>(consumer, loopback);
        return buildCountedScheduleSink(sink, deserializer, redeliverOnRecovery);
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
     * @param serializer serialises the message data, a {@code T}-value.
     * @param deserializer de-serialises the message data, a {@code T}-value.
     * @return the message sink.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws Exception if this sink couldn't be connected to the underlying
     * queue.
     */
    default MessageSink<QM, InputStream> buildRepeatSink(
            RepeatConsumer<T> processor,
            List<Duration> retryIntervals,
            Consumer<T> failureHandler,
            SinkWriter<T, OutputStream> serializer,
            SourceReader<InputStream, T> deserializer) throws Exception {
        Reschedulable<T> consumer =
                ReschedulableFactory.buildForRepeatConsumer(
                        processor, retryIntervals, failureHandler);
        return buildReschedulableSink(consumer, serializer, deserializer);
    }

}

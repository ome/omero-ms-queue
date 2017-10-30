package ome.smuggler.config.wiring.artemis;

import static java.util.Objects.requireNonNull;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.config.CoreQueueConfiguration;

import kew.core.msg.*;
import kew.core.qchan.QChannelFactory;
import kew.providers.artemis.ServerConnector;
import kew.providers.artemis.qchan.ArtemisMessage;
import kew.providers.artemis.qchan.ArtemisQChannelFactory;
import ome.smuggler.providers.json.JsonInputStreamReader;
import ome.smuggler.providers.json.JsonOutputStreamWriter;
import util.io.SinkWriter;
import util.io.SourceReader;

/**
 * Builds sources and sinks for queues holding {@code T}-values serialized to
 * JSON. That is, {@code T} is the type of the message data clients exchange
 * and {@code T}-values are stored in the queue as JSON. Under the bonnet, we
 * use Gson for serialization so we should be able to handle conversion to and
 * from JSON of most Java types you throw at us. (Well, within reason. There's
 * some corner cases with generics where serialization may fail but we write
 * serialization tests for each and every message type we use to avoid runtime
 * surprises!)
 * @param <T> the type of the message data.
 */
public class JsonQ<T> {

    private final QChannelFactory<ArtemisMessage, T> factory;
    private final CoreQueueConfiguration qConfig;
    private final Class<T> msgType;

    /**
     * Creates a new instance.
     * @param connector gateway to the Artemis server.
     * @param qConfig Artemis queue configuration.
     * @param msgType the class of the message data object to read.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public JsonQ(ServerConnector connector,
                 CoreQueueConfiguration qConfig,
                 Class<T> msgType) {
        requireNonNull(connector, "connector");
        requireNonNull(qConfig, "qConfig");
        requireNonNull(msgType, "msgType");

        this.factory = new ArtemisQChannelFactory<>(connector, qConfig);
        this.qConfig = qConfig;
        this.msgType = msgType;
    }

    /**
     * Builds a new {@link SinkWriter} to serialize {@code T}-values to JSON.
     * @return the serializer.
     */
    public SinkWriter<T, OutputStream> serializer() {
        return new JsonOutputStreamWriter<>();
    }

    /**
     * Builds a new {@link SourceReader} to deserialize {@code T}-values from
     * JSON.
     * @return the deserializer.
     */
    public SourceReader<InputStream, T> deserializer() {
        return new JsonInputStreamReader<>(msgType);
    }

    /**
     * Adds our queue configuration to the Artemis core configuration.
     * This has to be done before starting the server.
     * @param cfg Artemis core configuration.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public void deploy(Configuration cfg) {
        requireNonNull(cfg, "cfg");
        cfg.addQueueConfiguration(qConfig);
    }

    /**
     * Builds a channel source to send {@code T}-values.
     * @return the channel source.
     * @throws Exception if an error occurred while building the channel.
     */
    public ChannelSource<T> sourceChannel() throws Exception {
        return factory.buildSource(serializer());
    }

    /**
     * Builds a message sink to receive messages and pass them on as {@code
     * T}-values to the specified consumer.
     * @param sink consumes data received from the queue.
     * @return the message sink.
     * @throws Exception if an error occurred while building the channel.
     */
    public MessageSink<ArtemisMessage, InputStream> sinkChannel(
            ChannelSink<T> sink)
                throws Exception {
        return factory.buildSink(sink, deserializer());
    }

    /**
     * Builds a message sink to receive messages and pass them on as {@code
     * T}-values to the specified repeat consumer.
     * @param processor consumes data received from the queue.
     * @param retryIntervals intervals at which to re-deliver a message the
     *                       processor failed to consume successfully.
     * @param failureHandler handles a message the processor wasn't able to
     *                       consume successfully after trying for the number
     *                       of times specified by the retry intervals.
     * @return the message sink.
     * @throws Exception if an error occurred while building the channel.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public MessageSink<ArtemisMessage, InputStream> dequeueTask(
            RepeatConsumer<T> processor,
            List<Duration> retryIntervals,
            Consumer<T> failureHandler)
                throws Exception {
        Reschedulable<T> consumer =
                ReschedulableFactory.buildForRepeatConsumer(processor,
                                                            retryIntervals,
                                                            failureHandler);
        return factory.buildReschedulableSink(consumer,
                                              serializer(),
                                              deserializer());
    }

}

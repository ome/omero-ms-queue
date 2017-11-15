package kew.providers.artemis.qchan;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.activemq.artemis.core.config.CoreQueueConfiguration;

import kew.core.qchan.QChannelFactoryAdapter;
import kew.providers.artemis.ServerConnector;
import util.io.SinkWriter;
import util.io.SourceReader;

/**
 * Convenience class to tie together an Artemis queue with factory methods to
 * create channel sources and sinks.
 */
public class ArtemisQChannel<T>
        extends QChannelFactoryAdapter<ArtemisMessage, T> {

    /**
     * Creates a new instance.
     * @param connector    gateway to the Artemis server.
     * @param qConfig      the queue configuration.
     * @param serializer   serialises the message data, a {@code T}-value.
     * @param deserializer de-serialises the message data, a {@code T}-value.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public ArtemisQChannel(ServerConnector connector,
                           CoreQueueConfiguration qConfig,
                           SinkWriter<T, OutputStream> serializer,
                           SourceReader<InputStream, T> deserializer) {
        super(new ArtemisQChannelFactory<>(connector, qConfig),
              serializer,
              deserializer);
    }

}

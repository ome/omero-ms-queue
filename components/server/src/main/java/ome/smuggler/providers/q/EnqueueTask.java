package ome.smuggler.providers.q;

import static java.util.Objects.requireNonNull;

import java.io.OutputStream;

import kew.core.qchan.spi.QMsgBuilder;
import kew.core.qchan.spi.QMsgFactory;
import kew.core.qchan.spi.QProducer;
import kew.core.msg.ChannelMessage;
import kew.core.msg.MessageSource;
import util.io.SinkWriter;

/**
 * Puts messages on a queue, asynchronously.
 * MetaProps are durable by default but any other kind of message can be
 * constructed by providing a message builder function as message metadata.
 *
 * @param <QM> the message type in the underlying middleware.
 * @param <T> the type of the message data.
 */
public class EnqueueTask<QM, T>
    implements MessageSource<QMsgBuilder<QM>, T> {

    private final QProducer<QM> producer;
    private final SinkWriter<T, OutputStream> serializer;

    /**
     * Creates a new instance.
     * @param producer provides access to the queue on which to put messages.
     * @param serializer serialises the message data, a {@code T}-value.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public EnqueueTask(QProducer<QM> producer,
                       SinkWriter<T, OutputStream> serializer) {
        requireNonNull(producer, "producer");
        requireNonNull(serializer, "serializer");
        
        this.producer = producer;
        this.serializer = serializer;
    }

    private void writeBody(OutputStream out,
                           ChannelMessage<QMsgBuilder<QM>, T> msg)
            throws Exception {
        T data = msg.data();
        serializer.write(out, data);
    }

    @Override
    public void send(ChannelMessage<QMsgBuilder<QM>, T> msg) throws Exception {
        requireNonNull(msg, "msg");

        QMsgBuilder<QM> messageBuilder =
                msg.metadata().orElse(QMsgFactory::durableMessage);
        producer.sendMessage(
                   messageBuilder,
                   out -> writeBody(out, msg));
    }

}

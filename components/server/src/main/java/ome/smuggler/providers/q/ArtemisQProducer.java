package ome.smuggler.providers.q;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.providers.q.MessageBodyWriter.writeBody;

import java.io.OutputStream;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.api.core.client.ClientProducer;

import kew.core.qchan.spi.QMsgBuilder;
import kew.core.qchan.spi.QMsgFactory;
import kew.core.qchan.spi.QProducer;
import util.lambda.ConsumerE;


/**
 * Puts messages on an Artemis queue.
 */
public class ArtemisQProducer implements QProducer<ArtemisMessage> {

    private final ClientProducer producer;
    private final QMsgFactory<ArtemisMessage> msgFactory;

    /**
     * Creates a new instance.
     * @param producer the Artemis producer bound to the queue where we're
     *                going to put messages.
     * @param msgFactory the factory to create Artemis messages.
     * @throws ActiveMQException if the session couldn't create a producer.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public ArtemisQProducer(ClientProducer producer,
                            QMsgFactory<ArtemisMessage> msgFactory)
            throws ActiveMQException {
        requireNonNull(producer, "producer");
        requireNonNull(msgFactory, "msgFactory");

        this.producer = producer;
        this.msgFactory = msgFactory;
    }

    @Override
    public void sendMessage(QMsgBuilder<ArtemisMessage> metadataBuilder,
                            ConsumerE<OutputStream> payloadWriter)
            throws ActiveMQException {
        requireNonNull(metadataBuilder, "metadataBuilder");
        requireNonNull(payloadWriter, "payloadWriter");

        ClientMessage msg = metadataBuilder.apply(msgFactory)
                                           .message();
        writeBody(msg, payloadWriter);

        producer.send(msg);
    }

}

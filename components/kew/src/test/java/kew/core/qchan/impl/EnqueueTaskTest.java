package kew.core.qchan.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static kew.core.msg.ChannelMessage.message;

import kew.core.msg.ChannelMessage;
import kew.core.qchan.spi.QMessageType;
import kew.core.qchan.spi.QMsgBuilder;
import kew.core.qchan.spi.QMsgFactory;
import kew.core.qchan.spi.QProducer;
import org.junit.Before;
import org.junit.Test;
import util.io.SinkWriter;
import util.lambda.ConsumerE;

import java.io.OutputStream;


public class EnqueueTaskTest
        implements SinkWriter<String, OutputStream>, QProducer<TestQMsg> {

    private TestQMsg builtMsg;
    private String sentMsgData;
    private EnqueueTask<TestQMsg, String> target;

    @Override
    public void write(OutputStream sink, String value) {
        sentMsgData = value;
    }

    @Override
    public void sendMessage(QMsgBuilder<TestQMsg> metadataBuilder,
                            ConsumerE<OutputStream> payloadWriter) {
        builtMsg = metadataBuilder.apply(new TestQMsgFactory());
        payloadWriter.accept(null);  // should call write method above
    }

    @Before
    public void setup() {
        builtMsg = null;
        sentMsgData = null;
        target = new EnqueueTask<>(this, this);
    }

    @Test
    public void sendMessageDataOnly() throws Exception {
        String msgData = "data";
        ChannelMessage<QMsgBuilder<TestQMsg>, String> msg = message(msgData);
        target.send(msg);

        assertNotNull(builtMsg);
        assertNotNull(sentMsgData);
        assertThat(builtMsg.type, is(QMessageType.Durable));
        assertThat(sentMsgData, is(msgData));
    }

    @Test
    public void sendNonDurableMessage() throws Exception {
        String msgData = "xxx";

        ChannelMessage<QMsgBuilder<TestQMsg>, String> msg =
                message(QMsgFactory::nonDurableMessage, msgData);
        target.send(msg);

        assertNotNull(builtMsg);
        assertNotNull(sentMsgData);
        assertThat(builtMsg.type, is(QMessageType.NonDurable));
        assertThat(sentMsgData, is(msgData));
    }

    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfNullQ() {
        new EnqueueTask<>(null, this);
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullWriter() throws Exception {
        new EnqueueTask<>(this, null);
    }

}

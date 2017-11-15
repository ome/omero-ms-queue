package kew.core.qchan.impl;

import static kew.core.msg.ChannelMessage.message;

import org.junit.Before;
import org.junit.Test;

import kew.core.msg.ChannelMessage;
import kew.core.qchan.spi.QMessageType;
import kew.core.qchan.spi.QMsgBuilder;
import kew.core.qchan.spi.QMsgFactory;


public class EnqueueTaskTest extends BaseSendTest {

    private EnqueueTask<TestQMsg, String> target;

    @Before
    public void setup() {
        super.setup();
        target = new EnqueueTask<>(this, this);
    }

    @Test
    public void sendMessageDataOnly() throws Exception {
        String msgData = "data";
        ChannelMessage<QMsgBuilder<TestQMsg>, String> msg = message(msgData);
        target.send(msg);

        assertHasSentMessage(QMessageType.Durable, msgData);
    }

    @Test
    public void sendNonDurableMessage() throws Exception {
        String msgData = "xxx";
        ChannelMessage<QMsgBuilder<TestQMsg>, String> msg =
                message(QMsgFactory::nonDurableMessage, msgData);
        target.send(msg);

        assertHasSentMessage(QMessageType.NonDurable, msgData);
    }

    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfNullProducer() {
        new EnqueueTask<>(null, this);
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullSerializer() throws Exception {
        new EnqueueTask<>(this, null);
    }

}

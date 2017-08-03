package kew.core.qchan.impl;

import static kew.core.msg.ChannelMessage.message;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Test;

import kew.core.msg.ChannelMessage;
import kew.core.msg.MessageSink;
import kew.core.msg.ChannelSink;
import util.io.SourceReader;

public class DequeueTaskTest extends BaseReceiveTest {

    private ChannelMessage<TestQMsg, InputStream> newMessage() {
        return message(new TestQMsg(), new ByteArrayInputStream(new byte[0]));
    }

    private DequeueTask<TestQMsg, String> newTarget() throws Exception {
        return new DequeueTask<>(qConnector, this, this, redeliverOnCrash);
    }

    @Test
    public void receiveMessageWithRedeliveryOnCrash() throws Exception {
        redeliverOnCrash = true;
        newTarget().consume(newMessage());
        assertHasReceivedMessage();
    }

    @Test
    public void receiveMessageWithoutRedeliveryOnCrash() throws Exception {
        redeliverOnCrash = false;
        newTarget().consume(newMessage());
        assertHasReceivedMessage();
    }

    @Test (expected = NullPointerException.class)
    public void ctor1ThrowsIfNullQ() throws Exception {
        ChannelSink<String> consumer = d -> {};
        new DequeueTask<>(null, consumer, this, true);
    }

    @Test (expected = NullPointerException.class)
    public void ctor1ThrowsIfNullConsumer() throws Exception {
        new DequeueTask<>(qConnector, (ChannelSink<String>) null, this, true);
    }

    @Test (expected = NullPointerException.class)
    public void ctor1ThrowsIfNullDeserializer() throws Exception {
        ChannelSink<String> consumer = d -> {};
        new DequeueTask<>(qConnector, consumer, null, true);
    }

    @Test (expected = NullPointerException.class)
    public void ctor2ThrowsIfNullQ() throws Exception {
        new DequeueTask<>(null, this, this, true);
    }

    @Test (expected = NullPointerException.class)
    public void ctor2ThrowsIfNullConsumer() throws Exception {
        new DequeueTask<>(qConnector, (MessageSink<TestQMsg, String>) null,
                          this, true);
    }

    @Test (expected = NullPointerException.class)
    public void ctor2ThrowsIfNullDeserializer() throws Exception {
        new DequeueTask<>(qConnector, this,
                (SourceReader<InputStream, String>) null, true);
    }

}

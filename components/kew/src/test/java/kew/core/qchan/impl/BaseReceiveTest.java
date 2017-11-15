package kew.core.qchan.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.InputStream;

import kew.core.qchan.spi.QConsumer;
import org.junit.Before;

import kew.core.msg.ChannelMessage;
import kew.core.msg.MessageSink;
import kew.core.qchan.spi.QConnector;
import util.io.SourceReader;


public class BaseReceiveTest
        implements MessageSink<TestQMsg, String>,
                   SourceReader<InputStream, String> {

    protected QConnector<TestQMsg> qConnector;
    protected QConsumer<TestQMsg> qConsumer;
    protected ChannelMessage<TestQMsg, String> receivedMsg;
    protected String dataToReceive;
    protected boolean redeliverOnRecovery;
    protected RuntimeException simulateConsumerCrash;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        receivedMsg = null;
        simulateConsumerCrash = null;
        dataToReceive = "data";

        qConnector = mock(QConnector.class);
        qConsumer = mock(QConsumer.class);
        when(qConnector.newConsumer(any())).thenReturn(qConsumer);
    }

    @Override
    public String read(InputStream source) throws Exception {
        return dataToReceive;  // DequeueTask will set it in the msg passed
                               // to the consume method below
    }

    @Override
    public void consume(ChannelMessage<TestQMsg, String> msg) {
        receivedMsg = msg;
        TestQMsg meta = receivedMetadata();
        if (redeliverOnRecovery) {
            assertNull(meta.removedFromQueue);
        } else {
            assertNotNull(meta.removedFromQueue);
            assertThat(meta.removedFromQueue, is(true));
        }
        if (simulateConsumerCrash != null)
            throw simulateConsumerCrash;
    }

    protected void assertHasReceivedMessage() {
        assertNotNull(receivedMsg);
        assertTrue(receivedMsg.metadata().isPresent());
        assertThat(receivedMsg.data(), is(dataToReceive));
    }

    protected TestQMsg receivedMetadata() {
        return receivedMsg.metadata().get();
    }

}

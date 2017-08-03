package kew.core.qchan.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.OutputStream;
import java.time.Duration;

import org.junit.Before;

import kew.core.qchan.spi.QMsgBuilder;
import kew.core.qchan.spi.QMessageType;
import kew.core.qchan.spi.QProducer;
import util.io.SinkWriter;
import util.lambda.ConsumerE;
import util.types.FutureTimepoint;


public class BaseSendTest
        implements SinkWriter<String, OutputStream>, QProducer<TestQMsg> {

    protected TestQMsg builtMsg;
    protected String sentMsgData;

    @Override
    public void write(OutputStream sink, String value) {
        sentMsgData = value;
    }

    @Override
    public void sendMessage(QMsgBuilder<TestQMsg> metadataBuilder,
                            ConsumerE<OutputStream> payloadWriter) {
        builtMsg = metadataBuilder.apply(new TestQMsgFactory());
        payloadWriter.accept(null);  // EnqueueTask should make it call write method above
    }

    @Before
    public void setup() {
        builtMsg = null;
        sentMsgData = null;
    }

    protected void assertHasSentMessage(QMessageType t, String originalMsgData) {
        assertNotNull(builtMsg);
        assertNotNull(sentMsgData);
        assertThat(builtMsg.type, is(t));
        assertThat(sentMsgData, is(originalMsgData));
    }

    protected void assertScheduleIsNow() {
        assertNotNull(builtMsg.schedule);

        Duration now = FutureTimepoint.now().get();
        Duration schedule = builtMsg.schedule.get();
        assertThat(now.minus(Duration.ofSeconds(1)), lessThan(schedule));
    }

}

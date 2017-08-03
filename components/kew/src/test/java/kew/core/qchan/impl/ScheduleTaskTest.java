package kew.core.qchan.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static kew.core.msg.ChannelMessage.message;

import java.time.Duration;

import org.junit.Before;
import org.junit.Test;

import kew.core.msg.ChannelMessage;
import kew.core.qchan.spi.QMessageType;
import util.types.FutureTimepoint;

public class ScheduleTaskTest extends BaseSendTest {

    private ScheduleTask<TestQMsg, String> target;

    @Before
    public void setup() {
        super.setup();
        target = new ScheduleTask<>(this, this);
    }

    @Test
    public void sendMessageDataOnly() throws Exception {
        String msgData = "data";
        ChannelMessage<FutureTimepoint, String> msg = message(msgData);
        target.send(msg);

        assertHasSentMessage(QMessageType.Durable, msgData);
        assertScheduleIsNow();
    }

    @Test
    public void sendMessageWithSchedule() throws Exception {
        String msgData = "data";
        FutureTimepoint tomorrow = new FutureTimepoint(Duration.ofDays(1));
        ChannelMessage<FutureTimepoint, String> msg =
                message(tomorrow, msgData);
        target.send(msg);

        assertHasSentMessage(QMessageType.Durable, msgData);
        assertThat(builtMsg.schedule, is(tomorrow));
    }

    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfNullProducer() {
        new ScheduleTask<>(null, this);
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullSerializer() throws Exception {
        new ScheduleTask<>(this, null);
    }

}

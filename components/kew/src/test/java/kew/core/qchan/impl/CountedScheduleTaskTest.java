package kew.core.qchan.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static kew.core.msg.ChannelMessage.message;

import java.time.Duration;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import kew.core.msg.ChannelMessage;
import kew.core.msg.CountedSchedule;
import kew.core.qchan.spi.QMessageType;
import util.types.FutureTimepoint;
import util.types.PositiveN;

public class CountedScheduleTaskTest extends BaseSendTest {

    private CountedScheduleTask<TestQMsg, String> target;

    @Before
    public void setup() {
        super.setup();
        target = new CountedScheduleTask<>(this, this);
    }

    private void assertScheduleCountIs(long expected) {
        Optional<Long> actual =
                builtMsg.lookupLongValue(MetaProps.ScheduleCountKey);

        assertNotNull(actual);
        assertTrue(actual.isPresent());
        assertThat(actual.get(), is(expected));
    }

    @Test
    public void sendMessageDataOnly() throws Exception {
        String msgData = "data";
        ChannelMessage<CountedSchedule, String> msg = message(msgData);
        target.send(msg);

        assertHasSentMessage(QMessageType.Durable, msgData);
        assertScheduleIsNow();
        assertScheduleCountIs(1);
    }

    @Test
    public void sendMessageWithCountedSchedule() throws Exception {
        String msgData = "data";
        FutureTimepoint tomorrow = new FutureTimepoint(Duration.ofDays(1));
        PositiveN count = PositiveN.of(2);
        CountedSchedule metadata = new CountedSchedule(tomorrow, count);
        ChannelMessage<CountedSchedule, String> msg =
                message(metadata, msgData);
        target.send(msg);

        assertHasSentMessage(QMessageType.Durable, msgData);
        assertThat(builtMsg.schedule, is(tomorrow));
        assertScheduleCountIs(count.get());
    }

    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfNullProducer() {
        new CountedScheduleTask<>(null, this);
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullSerializer() throws Exception {
        new CountedScheduleTask<>(this, null);
    }

}

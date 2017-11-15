package kew.core.qchan.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static kew.core.msg.ChannelMessage.message;

import org.junit.Test;

import kew.core.msg.ChannelMessage;
import kew.core.msg.CountedSchedule;
import kew.core.msg.MessageSink;


public class CountedScheduleSinkTest
        implements MessageSink<CountedSchedule, String> {

    private ChannelMessage<CountedSchedule, String> consumerMsg;
    private final String dataToReceive = "data";

    @Override
    public void consume(ChannelMessage<CountedSchedule, String> msg) {
        consumerMsg = msg;
    }

    private CountedScheduleSink<TestQMsg, String> newTarget() {
        return new CountedScheduleSink<>(this);
    }

    private ChannelMessage<TestQMsg, String> newMessageWith(
            long scheduleCount) {
        TestQMsg metadata = new TestQMsg();
        metadata.putProp(MetaProps.ScheduleCountKey, scheduleCount);

        return message(metadata, dataToReceive);
    }

    private void assertHasReceivedMessage() {
        assertNotNull(consumerMsg);
        assertThat(consumerMsg.data(), is(dataToReceive));
    }

    @Test
    public void receiveMsgWithoutScheduleCount() {
        TestQMsg metadata = new TestQMsg();
        ChannelMessage<TestQMsg, String> queued =
                message(metadata, dataToReceive);

        newTarget().consume(queued);

        assertHasReceivedMessage();
        assertFalse(consumerMsg.metadata().isPresent());
    }

    @Test
    public void receiveMsgWithPositiveScheduleCount() {
        long scheduleCount = 2;
        ChannelMessage<TestQMsg, String> queued = newMessageWith(scheduleCount);

        newTarget().consume(queued);

        assertHasReceivedMessage();
        assertTrue(consumerMsg.metadata().isPresent());
        assertThat(consumerMsg.metadata().get().count(), is(scheduleCount));
    }

    @Test (expected = IllegalArgumentException.class)
    public void receiveMsgWithZeroScheduleCount() {
        ChannelMessage<TestQMsg, String> queued = newMessageWith(0);
        newTarget().consume(queued);
    }

    @Test (expected = IllegalArgumentException.class)
    public void receiveMsgWithNegativeScheduleCount() {
        ChannelMessage<TestQMsg, String> queued = newMessageWith(-1);
        newTarget().consume(queued);
    }

    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfNullConsumer() {
        new CountedScheduleSink<>(null);
    }

}

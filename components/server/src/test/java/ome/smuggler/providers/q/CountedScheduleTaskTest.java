package ome.smuggler.providers.q;

import static org.mockito.Mockito.*;
import static kew.core.msg.ChannelMessage.message;

import java.time.Duration;

import kew.core.qchan.CountedScheduleTask;
import kew.core.qchan.MetaProps;
import org.apache.activemq.artemis.api.core.Message;
import org.junit.Test;

import kew.core.msg.CountedSchedule;
import kew.core.msg.MessageSource;
import util.types.FutureTimepoint;
import util.types.PositiveN;


public class CountedScheduleTaskTest extends BaseSendTest {
    
    private MessageSource<CountedSchedule, String> newTask() throws Exception {
        initMocks();
        when(msgToQueue.putLongProperty(anyString(), anyLong()))
        .thenReturn(msgToQueue);
        
        return new CountedScheduleTask<>(connector.newProducer(), (v, s) -> {});
    }
    
    @Test
    public void sendMessage() throws Exception {
        newTask().asDataSource().send("msg");

        verify(producer).send(msgToQueue);
    }
    
    @Test
    public void scheduleMessage() throws Exception {
        FutureTimepoint when = new FutureTimepoint(Duration.ofMinutes(1));
        long expectedSchedule = when.get().toMillis();
        long expectedCount = 1;
        PositiveN count = PositiveN.of(expectedCount);
        CountedSchedule metadata = new CountedSchedule(when, count);
        
        newTask().send(message(metadata, "msg"));
        
        verify(msgToQueue).putLongProperty(
                eq(Message.HDR_SCHEDULED_DELIVERY_TIME.toString()), 
                eq(expectedSchedule));
        verify(msgToQueue).putLongProperty(
                eq(MetaProps.ScheduleCountKey),
                eq(expectedCount));
        verify(producer).send(msgToQueue);
    }
    
    @Test (expected = NullPointerException.class)
    public void throwIfCtorArg1Null() {
        new CountedScheduleTask<>(null, (v, s) -> {});
    }

    @Test (expected = NullPointerException.class)
    public void throwIfCtorArg2Null() throws Exception {
        new CountedScheduleTask<>(connector.newProducer(), null);
    }

}

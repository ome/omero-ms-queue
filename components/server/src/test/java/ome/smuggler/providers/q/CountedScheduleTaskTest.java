package ome.smuggler.providers.q;

import static org.mockito.Mockito.*;
import static ome.smuggler.core.msg.ChannelMessage.message;

import java.time.Duration;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.Message;
import org.junit.Test;

import ome.smuggler.core.msg.CountedSchedule;
import ome.smuggler.core.msg.MessageSource;
import ome.smuggler.core.types.FutureTimepoint;
import ome.smuggler.core.types.PositiveN;


public class CountedScheduleTaskTest extends BaseSendTest {
    
    private MessageSource<CountedSchedule, String> newTask() throws ActiveMQException {
        initMocks();
        when(msgToQueue.putLongProperty(anyString(), anyLong()))
        .thenReturn(msgToQueue);
        
        return new CountedScheduleTask<>(connector, (v, s) -> {});
    }
    
    @Test
    public void sendMessage() throws ActiveMQException {
        newTask().asDataSource().uncheckedSend("msg");

        verify(producer).send(msgToQueue);
    }
    
    @Test
    public void scheduleMessage() throws ActiveMQException {
        FutureTimepoint when = new FutureTimepoint(Duration.ofMinutes(1));
        long expectedSchedule = when.get().toMillis();
        long expectedCount = 1;
        PositiveN count = PositiveN.of(expectedCount);
        CountedSchedule metadata = new CountedSchedule(when, count);
        
        newTask().uncheckedSend(message(metadata, "msg"));
        
        verify(msgToQueue).putLongProperty(
                eq(Message.HDR_SCHEDULED_DELIVERY_TIME.toString()), 
                eq(expectedSchedule));
        verify(msgToQueue).putLongProperty(
                eq(Messages.ScheduleCountKey), 
                eq(expectedCount));
        verify(producer).send(msgToQueue);
    }
    
    @Test (expected = NullPointerException.class)
    public void throwIfCtorArg1Null() throws ActiveMQException {
        new CountedScheduleTask<>(null, (v, s) -> {});
    }

    @Test (expected = NullPointerException.class)
    public void throwIfCtorArg2Null() throws ActiveMQException {
        new CountedScheduleTask<>(connector, null);
    }

}

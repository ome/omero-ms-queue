package ome.smuggler.providers.q;

import static org.mockito.Mockito.*;
import static ome.smuggler.core.msg.ChannelMessage.message;

import java.time.Duration;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.Message;
import org.junit.Test;

import ome.smuggler.core.msg.SchedulingSource;
import util.types.FutureTimepoint;


public class ScheduleTaskTest extends BaseSendTest {
    
    private SchedulingSource<String> newTask() throws ActiveMQException {
        initMocks();
        when(msgToQueue.putLongProperty(anyString(), anyLong()))
        .thenReturn(msgToQueue);
        
        return new ScheduleTask<>(connector, (v, s) -> {});
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
        
        newTask().uncheckedSend(message(when, "msg"));
        
        verify(msgToQueue).putLongProperty(
                eq(Message.HDR_SCHEDULED_DELIVERY_TIME.toString()), 
                eq(expectedSchedule));
        verify(producer).send(msgToQueue);
    }
    
    @Test (expected = NullPointerException.class)
    public void throwIfCtorArg1Null() throws ActiveMQException {
        new ScheduleTask<>(null, (v, s) -> {});
    }

    @Test (expected = NullPointerException.class)
    public void throwIfCtorArg2Null() throws ActiveMQException {
        new ScheduleTask<>(connector, null);
    }

}

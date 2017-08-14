package kew.providers.artemis.qchan;

import static org.mockito.Mockito.*;
import static kew.core.msg.ChannelMessage.message;

import java.time.Duration;

import kew.core.qchan.impl.ScheduleTask;
import org.apache.activemq.artemis.api.core.Message;
import org.junit.Test;

import kew.core.msg.SchedulingSource;
import util.types.FutureTimepoint;


public class ScheduleTaskTest extends BaseSendTest {
    
    private SchedulingSource<String> newTask() throws Exception {
        initMocks();
        when(msgToQueue.putLongProperty(anyString(), anyLong()))
        .thenReturn(msgToQueue);
        
        return new ScheduleTask<>(connector.newProducer(), (v, s) -> {});
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
        
        newTask().send(message(when, "msg"));
        
        verify(msgToQueue).putLongProperty(
                eq(Message.HDR_SCHEDULED_DELIVERY_TIME.toString()), 
                eq(expectedSchedule));
        verify(producer).send(msgToQueue);
    }

}

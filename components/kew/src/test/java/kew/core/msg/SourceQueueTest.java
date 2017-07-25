package kew.core.msg;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static kew.core.msg.ChannelMessage.message;

import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;


public class SourceQueueTest {

    private SourceQueue<String, Integer> queue;
    
    @Before
    public void setup() {
        queue = new SourceQueue<>();
    }
    
    @Test (expected = NullPointerException.class)
    public void sendThrowsIfNullData() throws Exception {
        queue.send(null);
    }
    
    @Test (expected = NullPointerException.class)
    public void sendThrowsIfNullMetadata() throws Exception {
        queue.send(message(null, 0));
    }
    
    @Test (expected = NullPointerException.class)
    public void sendThrowsIfHasMetadataButNullData() throws Exception {
        queue.send(message("", null));
    }
    
    @Test
    public void headReturnsEmptyIfQueueIsEmpty() {
        Optional<ChannelMessage<String, Integer>> actual = queue.head();
        
        assertNotNull(actual);
        assertFalse(actual.isPresent());
    }
    
    @Test
    public void headDataReturnsEmptyIfQueueIsEmpty() {
        Optional<Integer> actual = queue.headData();
        
        assertNotNull(actual);
        assertFalse(actual.isPresent());
    }
    
    @Test
    public void headReturnsEmptyMetaIfNoMetaWasSent() throws Exception {
        Integer data = 1;
        queue.asDataSource().send(data);
        Optional<ChannelMessage<String, Integer>> actual = queue.head();
        
        assertNotNull(actual);
        assertTrue(actual.isPresent());
        
        ChannelMessage<String, Integer> msg = actual.get();
        
        assertNotNull(msg.metadata());
        assertFalse(msg.metadata().isPresent());
        assertThat(msg.data(), is(data));
    }
    
    @Test
    public void headReturnsMetaIfMetaWasSent() throws Exception {
        String metadata = "m";
        Integer data = 1;
        queue.send(message(metadata, data));
        Optional<ChannelMessage<String, Integer>> actual = queue.head();
        
        assertNotNull(actual);
        assertTrue(actual.isPresent());
        
        ChannelMessage<String, Integer> msg = actual.get();
        
        assertNotNull(msg.metadata());
        assertTrue(msg.metadata().isPresent());
        assertThat(msg.metadata().get(), is(metadata));
        assertThat(msg.data(), is(data));
    }
    
    @Test
    public void queueMessages() throws Exception {
        String m1 = "m1", m2 = "m2";
        Integer d1 = 1, d2 = 2;
        queue.send(message(m1, d1));
        queue.send(message(m2, d2));
        
        ChannelMessage<String, Integer> msg = queue.head().get();
        assertThat(msg.metadata().get(), is(m1));
        assertThat(msg.data(), is(d1));
        
        msg = queue.head().get();
        assertThat(msg.metadata().get(), is(m2));
        assertThat(msg.data(), is(d2));
        
        assertFalse(queue.head().isPresent());
    }
    
    @Test
    public void dequeueAllReturnsEmptyIfQueueIsEmpty() {
        List<ChannelMessage<String, Integer>> actual = queue.dequeue();
        
        assertNotNull(actual);
        assertThat(actual.size(), is(0));
    }
    
    @Test
    public void dequeueData() throws Exception {
        Integer[] data = { 1,  2 };
        queue.asDataSource().send(data[0]);
        queue.asDataSource().send(data[1]);
        Integer[] actual = queue.dequeueData().toArray(new Integer[0]);
        
        assertArrayEquals(data, actual);
        assertFalse(queue.head().isPresent());
    }
    
}

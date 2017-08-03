package kew.core.msg;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static kew.core.msg.ChannelMessage.message;

import org.junit.Before;
import org.junit.Test;

public class MessageSinkTest {

    private Integer receivedData;

    @Before
    public void setup() {
        receivedData = null;
    }

    @Test (expected = NullPointerException.class)
    public void forwardThrowsIfNullTarget() {
        MessageSink.forwardDataTo(null);
    }

    @Test
    public void forwardDataToTarget() {
        MessageSink<String, Integer> sink =
                MessageSink.forwardDataTo(i -> receivedData = i);
        Integer data = 2;
        sink.consume(message(data));

        assertNotNull(receivedData);
        assertThat(receivedData, is(data));
    }

    @Test
    public void useAsDataSink() {
        MessageSink<String, Integer> sink = m -> receivedData = m.data();

        Integer data = 2;
        sink.asDataSink().consume(data);

        assertNotNull(receivedData);
        assertThat(receivedData, is(data));
    }

}

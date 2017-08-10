package kew.core.msg;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ChannelSinkTransformerTest implements ChannelSink<Integer> {

    private Integer consumedData;
    private ChannelSink<Integer> transformedSink;

    private Integer transform(Integer x) {
        return x + 1;
    }

    @Override
    public void consume(Integer data) {
        consumedData = data;
    }


    @Before
    public void setup() {
        consumedData = null;
        transformedSink = new ChannelSinkTransformer<>(this::transform, this);
    }

    @Test
    public void consumeTransformedData() {
        Integer input = 10;
        Integer expected = transform(input);
        transformedSink.consume(input);

        assertThat(consumedData, is(expected));
    }

    @Test (expected = NullPointerException.class)
    public void consumeThrowsIfNullData() {
        transformedSink.consume(null);
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullTransformer() {
        new ChannelSinkTransformer<>(null, this);
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullTarget() {
        new ChannelSinkTransformer<>(x -> x, null);
    }

}

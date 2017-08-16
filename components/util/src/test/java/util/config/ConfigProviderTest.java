package util.config;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.NoSuchElementException;
import java.util.stream.Stream;

public class ConfigProviderTest implements ConfigProvider<Integer> {

    private Stream<Integer> configValues;

    @Override
    public Stream<Integer> readConfig() {
        return configValues;
    }

    @Before
    public void setup() {
        configValues = null;
    }

    @Test (expected = NoSuchElementException.class)
    public void firstThrowsIfEmptyConfig() {
        configValues = Stream.empty();
        this.first();
    }

    @Test
    public void firstReadsHeadOfStream() {
        int head = 1;
        configValues = Stream.of(head, 2);

        assertThat(this.first(), is(head));
    }

}

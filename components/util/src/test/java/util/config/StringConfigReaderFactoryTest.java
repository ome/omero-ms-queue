package util.config;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import java.util.stream.Stream;

public class StringConfigReaderFactoryTest {

    enum TestE { A }


    @Test
    public void readBoolFromStringSource() {
        ConfigProvider<Boolean> reader = StringConfigReaderFactory.makeBool(
                () -> Stream.of("true")
        );

        assertNotNull(reader);
        assertTrue(reader.first());
    }

    @Test
    public void readIntFromStringSource() {
        ConfigProvider<Integer> reader = StringConfigReaderFactory.makeInt(
                () -> Stream.of("1")
        );

        assertNotNull(reader);
        assertThat(reader.first(), is(1));
    }

    @Test
    public void readEnumFromStringSource() {
        ConfigProvider<TestE> reader = StringConfigReaderFactory.makeEnum(
                TestE.class,
                () -> Stream.of("A")
        );

        assertNotNull(reader);
        assertThat(reader.first(), is(TestE.A));
    }

    @Test
    public void ctor() {
        new StringConfigReaderFactory();  // only to get 100% coverage.
    }

}

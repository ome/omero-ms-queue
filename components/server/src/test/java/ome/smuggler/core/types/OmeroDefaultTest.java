package ome.smuggler.core.types;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class OmeroDefaultTest {

    @Test
    public void portMustMatchWhatSpecifiedInWebInterfaceComments() {
        String specifiedInComments = "4064";
        String actual = OmeroDefault.Port.toString();

        assertThat(actual, is(specifiedInComments));
    }

    @Test
    public void sessionTimeoutMustMatchWhatSpecifiedInWebInterfaceComments() {
        long specifiedInComments = 600000;
        long actual = OmeroDefault.SessionTimeout.toMillis();

        assertThat(actual, is(specifiedInComments));
    }

    @Test
    public void sessionKeepAliveIntervalIsHalfOfSessionTimeout() {
        long expected = OmeroDefault.SessionTimeout.toMillis() / 2 ;
        long actual = OmeroDefault.SessionKeepAliveInterval.toMillis();

        assertThat(actual, is(expected));
    }

}

package ome.smuggler.core.service.omero.impl;

import static ome.smuggler.core.service.omero.impl.OmeCliTestUtils.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.net.URI;

import org.junit.Test;


public class KeepAliveCommandBuilderTest {

    private static KeepAliveCommandBuilder newBuilder(URI omero,
                                                      String sessionKey) {
        return new KeepAliveCommandBuilder(OmeCliConfigBuilder.config(),
                omero, sessionKey);
    }


    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfFirstArgNull() {
        new KeepAliveCommandBuilder(null, omeroServer(), sessionKey());
    }
    
    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfSecondArgNull() {
        new KeepAliveCommandBuilder(OmeCliConfigBuilder.config(), null,
                                    sessionKey());
    }

    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfThirdArgNull() {
        new KeepAliveCommandBuilder(OmeCliConfigBuilder.config(), omeroServer(),
                                    null);
    }

    @Test
    public void verifyCommandName() {
        KeepAliveCommandBuilder target = newBuilder(omeroServer(), sessionKey());
        String name = commandName(target);

        assertThat(name, is("SessionKeepAlive"));
    }


    @Test
    public void minimalCommandLine() {
        URI omero = omeroServer();
        String sessionKey = sessionKey();
        KeepAliveCommandBuilder target = newBuilder(omero, sessionKey);
        String[] xs = commandArgs(target);

        assertThat(xs.length, is(3));
        assertThat(xs[0], is(omero.getHost()));
        assertThat(xs[1], is("" + omero.getPort()));
        assertThat(xs[2], is(sessionKey));
    }

}

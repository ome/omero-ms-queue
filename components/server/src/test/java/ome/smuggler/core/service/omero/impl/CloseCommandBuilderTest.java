package ome.smuggler.core.service.omero.impl;


import org.junit.Test;

import java.net.URI;

import static ome.smuggler.core.service.omero.impl.OmeCliTestUtils.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CloseCommandBuilderTest {

    private static CloseCommandBuilder newBuilder(URI omero,
                                                      String sessionKey) {
        return new CloseCommandBuilder(OmeCliConfigBuilder.config(),
                omero, sessionKey);
    }


    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfFirstArgNull() {
        new CloseCommandBuilder(null, omeroServer(), sessionKey());
    }

    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfSecondArgNull() {
        new CloseCommandBuilder(OmeCliConfigBuilder.config(), null,
                sessionKey());
    }

    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfThirdArgNull() {
        new CloseCommandBuilder(OmeCliConfigBuilder.config(), omeroServer(),
                null);
    }

    @Test
    public void verifyCommandName() {
        CloseCommandBuilder target = newBuilder(omeroServer(), sessionKey());
        String name = commandName(target);

        assertThat(name, is("CloseSession"));
    }

    @Test
    public void minimalCommandLine() {
        URI omero = omeroServer();
        String sessionKey = sessionKey();
        CloseCommandBuilder target = newBuilder(omero, sessionKey);
        String[] xs = commandArgs(target);

        assertThat(xs.length, is(3));
        assertThat(xs[0], is(omero.getHost()));
        assertThat(xs[1], is("" + omero.getPort()));
        assertThat(xs[2], is(sessionKey));
    }

}

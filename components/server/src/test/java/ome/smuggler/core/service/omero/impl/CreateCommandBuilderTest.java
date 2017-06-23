package ome.smuggler.core.service.omero.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static ome.smuggler.core.service.omero.impl.OmeCliTestUtils.*;

import org.junit.Test;

import java.net.URI;


public class CreateCommandBuilderTest {

    private static CreateCommandBuilder newBuilder(URI omero,
                                                   String user, String pass) {
        return new CreateCommandBuilder(OmeCliConfigBuilder.config(),
                                        omero, user, pass);
    }

    @Test
    public void verifyCommandName() {
        CreateCommandBuilder target = newBuilder(omeroServer(), "u", "p");
        String name = commandName(target);

        assertThat(name, is("CreateSession"));
    }

    @Test
    public void minimalCommandLine() {
        URI omero = omeroServer();
        String user = "u", pass = "p";
        CreateCommandBuilder target = newBuilder(omero, user, pass);
        String[] xs = commandArgs(target);

        assertThat(xs.length, is(4));
        assertThat(xs[0], is(omero.getHost()));
        assertThat(xs[1], is("" + omero.getPort()));
        assertThat(xs[2], is(user));
        assertThat(xs[3], is(pass));
    }

    @Test
    public void maskPassword() {
        String pass = "do not show!";
        CreateCommandBuilder target = newBuilder(omeroServer(), "u", pass);
        String printed = target.toString();

        assertThat(printed, not(containsString(pass)));
    }

    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfConfigArgNull() {
        new CreateCommandBuilder(null, omeroServer(), "u", "p");
    }

    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfServerArgNull() {
        newBuilder(null, "u", "p");
    }

    @Test(expected = IllegalArgumentException.class)
    public void ctorThrowsIfUserArgNull() {
        newBuilder(omeroServer(), null, "p");
    }

    @Test(expected = IllegalArgumentException.class)
    public void ctorThrowsIfUserArgEmpty() {
        newBuilder(omeroServer(), "", "p");
    }

    @Test(expected = IllegalArgumentException.class)
    public void ctorThrowsIfPassArgNull() {
        newBuilder(omeroServer(), "u", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ctorThrowsIfPassArgEmpty() {
        newBuilder(omeroServer(), "u", "");
    }

}

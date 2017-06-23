package ome.smuggler.core.service.omero.impl;

import static java.util.Objects.requireNonNull;
import static util.string.Strings.requireString;

import java.net.URI;

import ome.smuggler.core.types.OmeCliConfigSource;
import util.runtime.CommandBuilder;
import util.runtime.ListProgramArgument;
import util.runtime.jvm.JvmCmdBuilder;

/**
 * Builds the command line to call the OMERO create session command.
 */
public class CreateCommandBuilder extends OmeCliCommandBuilder {

    private final URI omero;
    private final String username;
    private final String password;

    /**
     * Creates a new instance to build a command line from the given data.
     *
     * @param config      configuration for the OMERO CLI commands.
     * @param omeroServer host and port of the OMERO server to use.
     * @param username the login name of the user this session is for.
     * @param password the password of the user this session is for.
     * @throws NullPointerException if the configuration or URI argument is
     * {@code null}.
     * @throws IllegalArgumentException if the username or password is {@code
     * null} or empty.
     */
    public CreateCommandBuilder(OmeCliConfigSource config, URI omeroServer,
                                String username, String password) {
        super(config);
        requireNonNull(omeroServer, "omeroServer");
        requireString(username, "username");
        requireString(password, "password");

        this.omero = omeroServer;
        this.username = username;
        this.password = password;
    }

    private ListProgramArgument<String> serverAndCredentials() {
        return arg(omero.getHost(),
                   String.valueOf(omero.getPort()),
                   username,
                   password);
    }

    @Override
    protected String commandName() {
        return "CreateSession";
    }

    @Override
    protected CommandBuilder assembleArguments(JvmCmdBuilder java) {
        return java.addApplicationArgument(serverAndCredentials());
    }

    @Override
    public String toString() {
        int passwordIndex = (int)tokens().count() - 1;
        return new OmeCliCommandPrinter(this).printMasking(passwordIndex);
    }

}

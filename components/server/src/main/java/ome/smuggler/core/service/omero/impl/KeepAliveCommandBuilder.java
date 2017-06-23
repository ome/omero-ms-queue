package ome.smuggler.core.service.omero.impl;


import static java.util.Objects.requireNonNull;

import java.net.URI;

import ome.smuggler.core.types.OmeCliConfigSource;
import util.runtime.ListProgramArgument;
import util.runtime.jvm.JvmCmdBuilder;

/**
 * Builds the command line to call the OMERO session keep-alive command.
 */
public class KeepAliveCommandBuilder extends OmeCliCommandBuilder {

    private final URI omero;
    private final String sessionKey;
    
    /**
     * Creates a new instance to build a command line from the given data.
     * @param config configuration for the OMERO CLI commands.
     * @param omeroServer host and port of the OMERO server to use.
     * @param sessionKey the session ID of the session to keep alive.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public KeepAliveCommandBuilder(OmeCliConfigSource config,
                                   URI omeroServer,
                                   String sessionKey) {
        super(config);
        requireNonNull(omeroServer, "omeroServer");
        requireNonNull(sessionKey, "sessionKey");
        
        this.omero = omeroServer;
        this.sessionKey = sessionKey;
    }

    private ListProgramArgument<String> serverAndKey() {
        return arg(omero.getHost(),
                   String.valueOf(omero.getPort()), 
                   sessionKey);
    }

    @Override
    protected String commandName() {
        return "SessionKeepAlive";
    }

    @Override
    protected JvmCmdBuilder assembleArguments(JvmCmdBuilder java) {
        return java.addApplicationArgument(serverAndKey());
    }

    @Override
    public String toString() {
        int sessionKeyIndex = (int)tokens().count() - 1;
        return new OmeCliCommandPrinter(this).printMasking(sessionKeyIndex);
    }

}

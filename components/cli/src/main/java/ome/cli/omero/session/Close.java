package ome.cli.omero.session;

import Glacier2.CannotCreateSessionException;
import Glacier2.PermissionDeniedException;
import ome.cli.cmd.*;
import omero.ServerError;
import omero.client;

import java.io.PrintStream;

import static java.util.Objects.requireNonNull;

/**
 * Command to close an existing session.
 * See {@link #main(String[]) main} method for how to execute this command.
 */
public class Close extends BaseCommand implements Command {

    private static CommandParser<Close> parser() {
        return args -> {
            requireArgs(args, 3);
            return new Close(hostArg(args), portArg(args), args[2]);
        };
    }

    /**
     * Call with the following required parameters on the command line, in this
     * order: {@code host port sessionKey}.
     * Optional parameters specified through system properties:
     * <ul>
     *     <li>{@code -Dinsecure=b}: b true (plain TCP) or false (SSL).</li>
     * </ul>
     * @param args {@code host port sessionKey}
     */
    public static void main(String[] args) {
        new CommandRunner<>(args, parser()).exec();
    }


    private final String sessionKey;

    private Close(String host, int port, String sessionKey) {
        super(host, port);
        requireNonNull(sessionKey, "sessionKey");

        this.sessionKey = sessionKey;
    }

    @Override
    public ExitCode exec(PrintStream out) throws CannotCreateSessionException,
            PermissionDeniedException, ServerError {
        client c = newClient();
        c.joinSession(sessionKey);
        c.killSession();

        return ExitCode.Ok;
    }

}

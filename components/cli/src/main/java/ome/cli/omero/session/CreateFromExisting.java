package ome.cli.omero.session;

import Glacier2.CannotCreateSessionException;
import Glacier2.PermissionDeniedException;
import ome.cli.cmd.*;
import omero.ServerError;
import omero.api.ServiceFactoryPrx;
import omero.client;
import omero.model.Details;
import omero.model.Session;
import omero.sys.Principal;

import java.io.PrintStream;

/**
 * Command to create a new session from an existing one.
 * A security exception will be raised if the existing session's user is not
 * in the "sudo" group.
 * See {@link #main(String[]) main} method for how to execute this command.
 */
public class CreateFromExisting extends BaseCommand implements Command {

    private static CommandParser<CreateFromExisting> parser() {
        return args -> {
            requireArgs(args, 3);
            return new CreateFromExisting(hostArg(args), portArg(args), args[2]);
        };
    }

    /**
     * Call with the following required parameters on the command line, in this
     * order: {@code host port sessionKey}.
     * Optional parameters specified through system properties:
     * <ul>
     *     <li>{@code -Dtimeout=n}: n seconds.</li>
     *     <li>{@code -Dinsecure=b}: b true (plain TCP) or false (SSL).</li>
     * </ul>
     * The new session key is output on {@code stdout}.
     * @param args {@code host port sessionKey}
     */
    public static void main(String[] args) {
        new CommandRunner<>(args, parser()).exec();
    }


    private final String sessionKey;

    private CreateFromExisting(String host, int port, String sessionKey) {
        super(host, port);
        this.sessionKey = sessionKey;
    }

    private Principal copyUser(Session existingSession) {
        Details current = existingSession.getDetails();
        Principal p = new Principal();

        p.eventType = "Sessions";
        p.name = current.getOwner().getOmeName().getValue();
        p.group = current.getGroup().getName().getValue();

        return p;
    }

    @Override
    public ExitCode exec(PrintStream out) throws CannotCreateSessionException,
            PermissionDeniedException, ServerError {
        client c = newClient();
        ServiceFactoryPrx serviceFactory = c.joinSession(sessionKey);
        Session existingSession = serviceFactory.getSessionService()
                                                .getSession(sessionKey);

        Session newSession = serviceFactory.getSessionService()
                .createSessionWithTimeouts(copyUser(existingSession), 0,
                                           timeToIdle(existingSession));
        
        out.print(newSession.getUuid().getValue());
        
        return ExitCode.Ok;
    }
}

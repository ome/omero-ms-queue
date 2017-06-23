package ome.cli.omero.session;

import Glacier2.CannotCreateSessionException;
import Glacier2.PermissionDeniedException;
import ome.cli.cmd.*;
import omero.ServerError;
import omero.api.ServiceFactoryPrx;
import omero.client;
import omero.model.Session;

import java.io.PrintStream;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * Command to create a new session.
 * See {@link #main(String[]) main} method for how to execute this command.
 */
public class Create extends BaseCommand implements Command {

    private static String group(Session existingSession) {
        return existingSession.getDetails().getGroup().getName().getValue();
    }

    private static CommandParser<Create> parser() {
        return args -> {
            requireArgs(args, 4);
            return new Create(hostArg(args), portArg(args), args[2], args[3]);
        };
    }

    /**
     * Call with the following required parameters on the command line, in this
     * order: {@code host port username password}.
     * Optional parameters specified through system properties:
     * <ul>
     * <li>{@code -Dtimeout=n}: n seconds.</li>
     * <li>{@code -Dinsecure=b}: b true (plain TCP) or false (SSL).</li>
     * </ul>
     * The new session key is output on {@code stdout}.
     *
     * @param args {@code host port sessionKey}
     */
    public static void main(String[] args) {
        new CommandRunner<>(args, parser()).exec();
    }


    private final String username;
    private final String password;


    private Create(String host, int port, String username, String password) {
        super(host, port);
        requireNonNull(username, "username");
        requireNonNull(password, "password");

        this.username = username;
        this.password = password;
    }

    protected Map<String, String> buildRequestContext() {
        Map<String, String> props = super.buildRequestContext();

        props.put("omero.user", String.valueOf(username));
        props.put("omero.pass", String.valueOf(password));

        return props;
    }

    @Override
    public ExitCode exec(PrintStream out) throws CannotCreateSessionException,
            PermissionDeniedException, ServerError {
        client c = newClient();
        ServiceFactoryPrx serviceFactory = c.createSession();
        serviceFactory.setSecurityPassword(password);

        Session initialSession = serviceFactory.getSessionService()
                                .getSession(c.getSessionId());
        Session newSession = serviceFactory.getSessionService()
                            .createUserSession(0,
                                    timeToIdle(initialSession),
                                    group(initialSession));
        c.killSession();  // close initial session.

        out.print(newSession.getUuid().getValue());

        return ExitCode.Ok;
    }

}

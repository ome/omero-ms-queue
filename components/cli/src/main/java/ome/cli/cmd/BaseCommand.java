package ome.cli.cmd;

import omero.IllegalArgumentException;
import omero.client;
import omero.model.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Common functionality shared by all {@link Command}s.
 */
public class BaseCommand {

    protected static void requireArgs(String[] args, int howMany) {
        if (args == null || args.length != howMany) {
            throw new IllegalArgumentException(
                    String.format("expected %s arguments", howMany));
        }
    }

    protected static String hostArg(String[] args) {
        return args[0];
    }

    protected static int portArg(String[] args) {
        return Integer.parseInt(args[1]);
    }

    protected static Optional<Integer> timeoutArg() {
        return new OptionalArg<>("timeout", Integer::parseInt).get();
    }

    protected static Optional<Boolean> insecureArg() {
        return new OptionalArg<>("insecure", Boolean::valueOf).get();
    }

    protected final String host;
    protected final int port;

    protected BaseCommand(String host, int port) {
        requireNonNull(host, "host");

        this.host = host;
        this.port = port;
    }

    private boolean isInsecure() {
        return insecureArg().orElse(false);
    }

    protected Map<String, String> buildRequestContext() {
        Map<String, String> props = new HashMap<>();

        props.put("omero.host", host);
        props.put("omero.port", String.valueOf(port));
        if (isInsecure()) {
            props.put("Ice.Default.Router",
                    "OMERO.Glacier2/router:tcp -p @omero.port@ -h @omero.host@");
        }

        return props;
    }

    protected long timeToIdle(Session existingSession) {
        return timeoutArg()
                .map(secs -> secs * 1000L)
                .orElse(existingSession.getTimeToIdle().getValue());
    }

    protected client newClient() {
        return new client(buildRequestContext());
    }

}

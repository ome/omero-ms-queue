package ome.cli;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Program's entry point providing centralised invocation of commands.
 * The {@link #main(String[]) main} method simply delegates to the command
 * specified as first argument.
 * Note that all commands come with a {@code main} method of their own, so it's
 * also possible to call a command directly, but this class comes in handy when
 * using a self-contained jar such as that you can create with the Spring Boot
 * Gradle plugin.
 */
public class Main {

    private static Consumer<String[]> lookupCommand(String[] args) {
        try {
            return Commands.valueOf(args[0]).mainMethod();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(64);
            return null;  // keep compiler happy.
        }
    }

    /**
     * Calls the specified command with the given arguments.
     * @param args the first argument on the command line must be the command's
     *             name exactly as given in the {@link Commands} enumeration.
     *             (If that is not the case execution is aborted with an exit
     *             code of {@code -1}.) Following the command's name are any
     *             arguments the command accepts.
     */
    public static void main(String[] args) {
        lookupCommand(args).accept(Arrays.copyOfRange(args, 1, args.length));
    }

}

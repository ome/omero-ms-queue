package ome.cli.cmd;

/**
 * Executes a {@link Command} to interact with OMERO.
 */
public class CommandRunner<T extends Command> {

    private final String[] commandLineArgs;
    private final CommandParser<T> parser;
    
    /**
     * Creates a new instance.
     * @param commandLineArgs the command-line arguments passed into the 
     * {@code main} method.
     * @param parser instantiates a command using the given command-line 
     * arguments.
     */
    public CommandRunner(String[] commandLineArgs, CommandParser<T> parser) {
        this.commandLineArgs = commandLineArgs;
        this.parser = parser;
    }
    
    private T createCommand() {
        try {
            return parser.parse(commandLineArgs);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(ExitCode.InvalidArgs.code());
            return null;  // keeps compiler happy.
        }
    }
    
    /**
     * Instantiates, executes the {@link Command}, and then terminates the JVM
     * process supplying the {@link ExitCode} returned by the command or the
     * one associated to a known exception that was thrown during the execution. 
     */
    public void exec() {
        ExitCode status;
        try {
            status = createCommand().exec(System.out);
        } catch (Exception e) {
            e.printStackTrace();
            status = ExitCode.codeFor(e);
        }
        System.exit(status.code());
    }
    
}

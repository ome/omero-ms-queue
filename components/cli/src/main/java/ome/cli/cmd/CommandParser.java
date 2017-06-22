package ome.cli.cmd;

/**
 * Instantiates a {@link Command} from the arguments that were passed on the
 * JVM command line.
 */
public interface CommandParser<T extends Command> {

    /**
     * Instantiates a new command using the arguments passed into the 
     * {@code main} method.
     * @param commandLineArgs the JVM command-line arguments.
     * @return the command.
     * @throws Exception if the command could not be instantiated using the
     * specified arguments, for example because of a malformed value.
     */
    T parse(String[] commandLineArgs) throws Exception;  // exception = bad args
    
}

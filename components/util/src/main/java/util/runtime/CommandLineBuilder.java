package util.runtime;

import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;

import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Base class to build tokenized command lines.
 */
public abstract class CommandLineBuilder implements CommandBuilder {

    protected final ProgramArgument<Path> programPath;
    
    protected CommandLineBuilder(ProgramArgument<Path> programPath) {
        requireNonNull(programPath, "programPath");
        this.programPath = programPath;
    }
    
    /**
     * Subclasses return the list of arguments that follow the program path
     * on the command line.
     * @return the arguments list; must not be {@code null} or contain any
     * {@code null}.
     */
    protected abstract Stream<CommandBuilder> arguments();

    @Override
    public Stream<String> tokens() {
        Stream<CommandBuilder> prog = Stream.of(programPath);
        Stream<CommandBuilder> args = requireNonNull(arguments(), "arguments");
        
        return Stream.of(prog, args)
                     .flatMap(identity())
                     .map(x -> requireNonNull(x, "null builder"))
                     .map(CommandBuilder::tokens)
                     .flatMap(identity());
    }
    
}

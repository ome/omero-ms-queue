package util.runtime.jvm;

import static java.util.Objects.requireNonNull;
import static util.sequence.Streams.concat;

import java.nio.file.Path;
import java.util.stream.Stream;

import util.runtime.CommandBuilder;
import util.runtime.ProgramArgument;

/**
 * Builds a command to launch a JVM with a class path and main class.
 */
public class MainClassCmdBuilder extends JvmCmdBuilder {
    
    private final CommandBuilder classPath;
    private final CommandBuilder mainClass;
    
    /**
     * Creates a new instance.
     * @param jvmPath the path to the JVM executable to use.
     * @param classPath the class path to pass to the JVM.
     * @param mainClass the main class to run.
     * @throws NullPointerException if any of the arguments is {@code null}.
     */
    public MainClassCmdBuilder(ProgramArgument<Path> jvmPath, 
                                ClassPathJvmArg classPath,
                                ProgramArgument<String> mainClass) {
        super(jvmPath);
        requireNonNull(classPath, "classPath");
        requireNonNull(mainClass, "mainClass");
        this.classPath = classPath;
        this.mainClass = mainClass;
    }

    @Override
    protected Stream<CommandBuilder> arguments() {
        return concat(
                Stream.of(classPath), sysProps.stream(), 
                Stream.of(mainClass), appArgs.stream());
    }
    
}

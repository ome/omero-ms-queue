package util.runtime.jvm;

import static java.util.Objects.requireNonNull;
import static util.sequence.Streams.concat;

import java.nio.file.Path;
import java.util.stream.Stream;

import util.runtime.CommandBuilder;
import util.runtime.ProgramArgument;

/**
 * Builds a command to launch a JVM with a jar file.
 */
public class JarCmdBuilder extends JvmCmdBuilder {
    
    private final CommandBuilder appJar;
    
    /**
     * Creates a new instance.
     * @param jvmPath the path to the JVM executable to use.
     * @param appJar the jar file to run.
     * @throws NullPointerException if any of the arguments is {@code null}.
     */
    public JarCmdBuilder(ProgramArgument<Path> jvmPath, JarJvmArg appJar) {
        super(jvmPath);
        requireNonNull(appJar, "appJar");
        this.appJar = appJar;
    }

    @Override
    protected Stream<CommandBuilder> arguments() {
        return concat(
                Stream.of(appJar), sysProps.stream(), appArgs.stream());
    }
    
}

package util.runtime.jvm;

import java.nio.file.Path;
import java.util.stream.Stream;

import util.runtime.BaseProgramArgument;

/**
 * Jar argument to pass to a JVM, e.g. {@code -jar app.jar}.
 */
public class JarJvmArg extends BaseProgramArgument<Path> {

    /**
     * Creates a new instance.
     */
    public JarJvmArg() {
        super();
    }
    
    /**
     * Creates a new instance to hold the specified argument.
     * @param appJarPath the app jar path.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public JarJvmArg(Path appJarPath) {
        super(appJarPath);
    }
    
    @Override
    protected Stream<String> tokenize(Path arg) {
        return Stream.of("-jar", arg.toString());
    }
    
}

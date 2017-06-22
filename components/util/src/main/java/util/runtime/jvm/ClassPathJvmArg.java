package util.runtime.jvm;

import java.util.stream.Stream;

import util.runtime.BaseProgramArgument;

/**
 * Class path argument to pass to a JVM, e.g. {@code -cp my.jar:yr.jar}.
 */
public class ClassPathJvmArg extends BaseProgramArgument<ClassPath> {
    
    /**
     * Creates a new instance.
     */
    public ClassPathJvmArg() {
        super();
    }
    
    /**
     * Creates a new instance to hold the specified argument.
     * @param arg the classpath argument.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public ClassPathJvmArg(ClassPath arg) {
        super(arg);
    }
    
    @Override
    protected Stream<String> tokenize(ClassPath arg) {
        return Stream.of("-cp", arg.toString());
    }
    
}

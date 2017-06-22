package util.runtime.jvm;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * The JVM command name on various platforms.
 */
public enum JvmName {

    /**
     * JVM command name on most Unix or Unix-like systems, e.g. Free BSD, AIX,
     * Linux, Mac OS X, etc.
     */
    Java("java"),
    
    /**
     * JVM command name on Windows.
     */
    JavaExe("java.exe");
    
    /**
     * Queries the runtime to find out what OS platform we're running on in
     * order to determine what is the expected name of the JVM command.
     * @return the expected JVM command name for this platform.
     */
    public static JvmName find() {
        return Optional.ofNullable(System.getProperty("os.name"))
                       .filter(n -> n.startsWith("Windows"))
                       .map(n -> JavaExe)
                       .orElse(Java);
    }
    
    private final String commandName;
    
    JvmName(String commandName) {
        this.commandName = commandName;
    }
    
    /**
     * @return the JVM command name as a (relative) {@link Path}, e.g. "java", 
     * "java.exe". 
     */
    public Path toPath() {
        return Paths.get(commandName);
    }
    
    /**
     * @return the JVM command name as a string, e.g. "java", "java.exe".
     */
    @Override
    public String toString() {
        return commandName;
    }
    
}

package util.runtime.jvm;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Locates the JVM executable.
 */
public class JvmLocator {
    
    /**
     * The path to the directory containing the executable of the currently 
     * running JVM.
     * This is given by the {@code java.home} system property and will be
     * different, in general, from the {@code JAVA_HOME} environment variable 
     * as the latter is usually set to the base installation directory whereas
     * the former is typically the JRE directory. Also note that if multiple 
     * JVM's are installed on the host, this method will always return the path
     * to the currently executing JVM which may be different from that of the 
     * default JVM on the system.
     * @return The absolute path to the directory containing this JVM's 
     * executable.
     */
    public static Path getCurrentJvmDir() {
        String javaHome = Optional.ofNullable(System.getProperty("java.home"))
                                  .orElse("");  // (*)
        return Paths.get(javaHome);
    }
    // (*) java.home should never be null, but better to be safe than sorry...
    
    /**
     * The absolute path to the executable of the currently running JVM.
     * This method returns a value only if the {@code java} command could be
     * determined and the file actually exists; an empty optional is returned 
     * otherwise.
     * @return the path, if it can be determined.
     */
    public static Optional<Path> findCurrentJvmExecutable() {
        String java = JvmName.find().toString();
        Stream<Path> candidates = Stream.of(  // paths relative to current jvm dir
                Paths.get("bin", java),       // most likely for Unixes and Win
                Paths.get("sh", java),        // possibly old AIX
                Paths.get(java)               // fallback
                );
        Path jvmDir = getCurrentJvmDir();
        
        return candidates.map(jvmDir::resolve)
                         .filter(path -> Files.exists(path))
                         .map(Path::normalize)
                         .map(Path::toAbsolutePath)
                         .findFirst();
    }
    /* NOTE. See also:
     * - http://stackoverflow.com/questions/1229605/is-this-really-the-best-way-to-start-a-second-jvm-from-java-code
     * - org.apache.tools.ant.util.JavaEnvUtils.getJreExecutable("java") 
     */
    
}

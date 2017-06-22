package ome.smuggler.config;

import static java.util.Objects.requireNonNull;
import static util.string.Strings.isNullOrEmpty;
import static util.string.Strings.requireString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

import util.object.Wrapper;
import util.string.Strings;

/**
 * Path to a directory used as a base for {@link #resolve(String) resolving} 
 * paths in configuration files.
 * The base directory path will be set to:
 * <ol>
 *     <li>the value of a specified system property, if the property exists
 *     and its value is a string of length greater than {@code 0}; otherwise
 *     </li>
 *     <li>the value of a specified environment variable, if such variable
 *     exists and its value is a string of length greater than {@code 0};
 *     otherwise</li>
 *     <li>the current working directory, i.e. the value of "user.dir".</li>
 * </ol>
 * Note that if the property or environment variable value is set to a relative
 * path, then that path will be resolved against the current working directory.
 */
public class BaseDir extends Wrapper<Path> {

    /**
     * Looks up the path associated to the specified key in the system 
     * properties. If no value is found for the key, the specified environment
     * variable is used instead. If no value is found for this variable either,
     * the current working directory is returned.
     * @param key the system property key.
     * @param varName the name of the environment variable.
     * @return the path.
     * @throws IllegalArgumentException if any argument is {@code null} or
     * empty.
     */
    public static Path lookup(String key, String varName) {
        requireString(key, "key");
        requireString(varName, "varName");

        return Stream.of(System.getProperty(key),
                         System.getenv(varName),
                         System.getProperty("user.dir"),
                         ".")
                .map(Strings::asOptional)
                .map(maybe -> maybe.map(Paths::get))
                .filter(Optional::isPresent)
                .findFirst()
                .get()
                .get();
    }
    
    /**
     * Sets a system property with the specified key and value.
     * @param key the key.
     * @param dir the value.
     * @throws IllegalArgumentException if the key is {@code null} or empty.
     * @throws NullPointerException if {@code dir} is {@code null}.
     */
    public static void store(String key, Path dir) {
        requireString(key, "key");
        requireNonNull(dir, "dir");
        
        System.setProperty(key, dir.toString());
    }
    
    /**
     * Creates a temporary directory and sets its path in the system properties 
     * using the given key. 
     * @param key the system property key.
     * @param dirNamePrefix name prefix of the temporary directory to create.
     * @return the path to the newly created temporary directory.
     * @throws IOException if the directory could not be created.
     * @throws IllegalArgumentException if any argument is {@code null} or 
     * empty.
     */
    public static Path storeTempDir(String key, String dirNamePrefix) 
            throws IOException {
        requireString(key, "key");
        requireString(dirNamePrefix, "dirNamePrefix");
        
        Path dir = Files.createTempDirectory(dirNamePrefix);
        System.setProperty(key, dir.toString());
        return dir;
    }

    /**
     * Creates a new instance using the path returned by the
     * {@link #lookup(String, String) lookup} method.
     * @param sysPropKey the system property key to pass to the
     *                   {@link #lookup(String, String) lookup} method.
     * @param envVarName the name of the environment variable to pass to the
     *                   {@link #lookup(String, String) lookup} method.
     * @throws IllegalArgumentException if any argument is {@code null} or empty.
     */
    public BaseDir(String sysPropKey, String envVarName) {
        super(lookup(sysPropKey, envVarName));
    }

    /**
     * Resolves the given path as follows.
     * If the argument is already an absolute path, then it is returned as is;
     * otherwise it is resolved against this base directory. In particular if
     * the path argument is {@code null} or empty, this base directory is
     * returned.
     * @param configuredPath a path in a configuration file.
     * @return the resolved path.
     */
    public Path resolve(String configuredPath) {
        if (isNullOrEmpty(configuredPath)) {
            return get();
        } else {
            return get().resolve(configuredPath);
        }
    }

    /**
     * Resolves the given path as follows.
     * If the argument is already an absolute path, then it is returned as is;
     * otherwise it is resolved against this base directory.
     * @param configuredPath a path in a configuration file.
     * @return the resolved path.
     * @throws IllegalArgumentException if the argument is {@code null} or 
     * empty.
     */
    public Path resolveRequiredPath(String configuredPath) {
        requireString(configuredPath, configuredPath);
        return get().resolve(configuredPath);
    }

}

package util.runtime.jvm;

import static java.util.Objects.requireNonNull;
import static util.error.Exceptions.unchecked;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Optional;


/**
 * Locates the base directory or jar file from which a given class was loaded.
 */
public class ClassPathLocator {

    private static URL extractResourceUrl(URL jar) throws IOException {
        JarURLConnection connection = (JarURLConnection) jar.openConnection();
        return connection.getJarFileURL();
    }
    
    private static URI fromUrl(URL sourceLocation) 
            throws IOException, URISyntaxException {
        String scheme = sourceLocation.getProtocol();
        if ("jar".equalsIgnoreCase(scheme)) {
            sourceLocation = extractResourceUrl(sourceLocation);
        }
        return sourceLocation.toURI();  // can't throw b/c we have a valid URL?
    }
    
    /**
     * Attempts to determine the base directory or jar file containing the 
     * given class.
     * This may fail for any of the reasons below:
     * <ul>
     *  <li>This JVM doesn't have the "getProtectionDomain" permission.</li>
     *  <li>{@code clazz} is not in a jar file or directory on the host.</li>
     * </ul>
     * Note that this method also works if the class is in a jar file contained
     * in another jar file and loaded through the Spring Boot class loader.
     * @param clazz a class to determine a containing jar file or directory.
     * @return an absolute path pointing to the class base location if the 
     * class could be determined to be in a local directory or jar file; 
     * an empty optional otherwise.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws SecurityException if a security manager exists and its 
     * the application doesn't have the "getProtectionDomain" permission.
     * <p>The following checked exceptions are rethrown as unchecked (i.e.
     * the exception is masked as a runtime exception and thrown as is without
     * wrapping it in a {@code RuntimeException}):
     * <br>{@link IOException} if an I/O error occurs when trying to locate
     * the enclosing jar if this class is in a jar within a jar, e.g. Spring
     * Boot self-contained jar.
     * </p>
     */
    public static Optional<Path> findBase(Class<?> clazz) {
        requireNonNull(clazz, "clazz");
        
        return Optional.ofNullable(clazz)
                       .map(Class::getProtectionDomain)
                       .map(ProtectionDomain::getCodeSource)  // javadoc says it may be null!
                       .map(CodeSource::getLocation)
                       .map(unchecked(ClassPathLocator::fromUrl))
                       .map(Paths::get)
                       .filter(Files::exists)
                       .map(Path::normalize)
                       .map(Path::toAbsolutePath);
    }

}

package util.runtime.jvm;

import static java.util.Objects.requireNonNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Optional;
import java.util.stream.Stream;

import util.sequence.Streams;

/**
 * Locates the base directory or jar file from which a given class was loaded.
 */
public class ClassPathLocator {

    // extract url part of a jar uri, see notes below.
    private static Optional<URI> extractJarResourceBase(URL resourceLocation) {
        return Optional.ofNullable(resourceLocation.toString())
                       .map(r -> r.split("!/"))
                       .map(Streams::pruneNull)   // shouldn't need this, but...
                       .flatMap(Stream::findFirst)
                       .map(r -> r.substring(4))  // strip prefix of "jar:"
                       .map(URI::create);         // throws IAE if invalid URI
    }
    /* NOTES
     * 1. Jar URI syntax. According to the JarURLConnection's JavaDoc, it is:
     *
     *     jar:url!/entry
     *
     * where url is a valid URL and entry is an optional path within the jar.
     * Examples:
     * - URLs referring to a whole jar
     *     jar:http://www.foo.com/bar/baz.jar!/
     *     jar:file:/home/duke/duke.jar!/
     * - pointing to a jar entry
     *     jar:http://www.foo.com/bar/baz.jar!/COM/foo/Quux.class
     * - SpringBoot nested weirdness
     *     jar:file:/data/libs/ome-smuggler-1.0.0.jar!/BOOT-INF/classes!/
     *
     * 2. Parsing. So I put together a poor's man parser to strip the url
     * component out of a jar URI based on the above syntax.
     *
     * 3. Gotchas. I used to have this code to extract the URL part:
     *
     *     JarURLConnection connection = (JarURLConnection) jar.openConnection();
     *     return connection.getJarFileURL();
     *
     * Which worked with SpringBoot 1.3.4 but stopped working in 1.5.1 cos of
     * the changes they made to
     *
     *     org.springframework.boot.loader.jar.JarURLConnection
     *
     * Long story short: it's best to do the parsing ourselves!
     */

    private static Optional<URI> extractFileResourceBase(URL resourceLocation) {
        try {
            return Optional.of(resourceLocation.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage(), e);  // (*)
        }
    }
    /* (*) Same as what URI::create does. So this method behaves in the same
     * way as extractJarResourceBase above if the URL is not a valid URI.
     */

    private static Optional<URI> findBaseURI(URL resourceLocation) {
        String p = resourceLocation.getProtocol();
        String scheme = p == null ? "" : p.toLowerCase();
        switch (scheme) {
            case "jar":  return extractJarResourceBase(resourceLocation);
            case "file": return extractFileResourceBase(resourceLocation);
            default:     return Optional.empty();
        }
    }

    /**
     * Attempts to find the filesystem path for the specified resource.
     * <p>The resource URL should be either a "file" or "jar" URL, typically
     * as returned by a class loader, that points to a file, usually a compiled
     * class or an application data file. In the case of a "file" URL, this
     * method attempts to convert it to a {@link Path}, whereas for a "jar"
     * URL this method attempts to extract the {@link Path} of the jar file
     * containing the resource. Note that in the case of nested jar files,
     * this method returns the {@link Path} to the outermost jar file, i.e.
     * the one visible on the filesystem.</p>
     * <p>In all cases, this method throw an exception if the resource URL
     * doesn't point to a local file. This method doesn't check that the
     * file actually exists, but the path must be local.</p>
     *
     * @param resourceLocation points to a resource either on the filesystem
     * or in a local jar file.
     * @return an absolute path pointing to the resource base location if the
     * resource could be determined to be in a local directory or jar file;
     * an empty optional otherwise.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws IllegalArgumentException if the resource URL is not a valid URI.
     */
    public static Optional<Path> toPath(URL resourceLocation) {
        requireNonNull(resourceLocation, "resourceLocation");

        return findBaseURI(resourceLocation)
              .map(Paths::get)
              .map(Path::normalize)
              .map(Path::toAbsolutePath);
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
     */
    public static Optional<Path> findBase(Class<?> clazz) {
        requireNonNull(clazz, "clazz");

        return Optional.of(clazz)
                       .map(Class::getProtectionDomain)
                       .map(ProtectionDomain::getCodeSource)  // javadoc says it may be null!
                       .map(CodeSource::getLocation)
                       .flatMap(ClassPathLocator::toPath)
                       .filter(Files::exists);
    }

}

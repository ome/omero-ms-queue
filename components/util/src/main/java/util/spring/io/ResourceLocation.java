package util.spring.io;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static util.sequence.Arrayz.hasNulls;
import static util.sequence.Arrayz.isNullOrZeroLength;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.stream.Stream;

import org.springframework.core.io.ResourceLoader;

import util.object.AbstractWrapper;

/**
 * A Spring resource location. 
 * This is, in general, a better alternative to using plain strings to locate
 * resources. 
 */
public class ResourceLocation extends AbstractWrapper<String> {

    /**
     * Builds a relative path location string from the path.
     * E.g. {@code relpath("x", "y") => x/y}.
     * @param pathComponents the path components. They will be converted to a
     * string using their {@code toString} method; any resulting empty string
     * will be removed from the path.
     * @return the location.
     * @throws IllegalArgumentException if the path component array is {@code 
     * null} or empty, or any of the path components is {@code null}, or all
     * path components evaluate to the empty string, or the path is not that 
     * of a valid {@link URI}.
     */
    public static ResourceLocation relpath(Object...pathComponents) {
        return new ResourceLocation("", false, pathComponents);
    }
    
    /**
     * Builds a file location string from the path.
     * E.g. {@code filepath("x", "y") => file:/x/y}.
     * @param pathComponents the path components. They will be converted to a
     * string using their {@code toString} method; any resulting empty string
     * will be removed from the path.
     * @return the location.
     * @throws IllegalArgumentException if the path component array is {@code 
     * null} or empty, or any of the path components is {@code null}, or all
     * path components evaluate to the empty string, or the path is not that 
     * of a valid {@link URI}.
     */
    public static ResourceLocation filepath(Object...pathComponents) {
        return new ResourceLocation("file:", pathComponents);
    }
    
    /**
     * Builds a relative file location string from the path; the path is 
     * interpreted relative to the current working directory.
     * E.g. {@code filepathFromCwd("x", "y") => file:./x/y}.
     * @param pathComponents the path components. They will be converted to a
     * string using their {@code toString} method; any resulting empty string
     * will be removed from the path.
     * @return the location.
     * @throws IllegalArgumentException if the path component array is {@code 
     * null} or empty, or any of the path components is {@code null}, or all
     * path components evaluate to the empty string, or the path is not that 
     * of a valid {@link URI}.
     */
    public static ResourceLocation filepathFromCwd(Object...pathComponents) {
        return new ResourceLocation("file:.", pathComponents);
    }
    
    /**
     * Builds a classpath location string from the path.
     * E.g. {@code classpath("x", "y") => classpath:/x/y}.
     * @param pathComponents the path components. They will be converted to a
     * string using their {@code toString} method; any resulting empty string
     * will be removed from the path.
     * @return the location.
     * @throws IllegalArgumentException if the path component array is {@code 
     * null} or empty, or any of the path components is {@code null}, or all
     * path components evaluate to the empty string, or the path is not that 
     * of a valid {@link URI}.
     */
    public static ResourceLocation classpath(Object...pathComponents) {
        return new ResourceLocation(ResourceLoader.CLASSPATH_URL_PREFIX, 
                                    pathComponents);
    }
    
    private static String build(String scheme, 
                                boolean absolute, 
                                Object...pathComponents) {
        String schemeSpecificPart = buildPath(absolute, pathComponents);
        try {
            URI location = new URI(scheme + schemeSpecificPart);
            return location.toString();
        } catch (URISyntaxException e) {
            String msg = String.format("%s: %s [%s]", 
                    "invalid path", schemeSpecificPart, e.getMessage());
            throw new IllegalArgumentException(msg, e);
        } 
    }
    
    private static String buildPath(boolean absolute, Object...pathComponents) {
        if (isNullOrZeroLength(pathComponents)) {
            throw new IllegalArgumentException("no path components");
        }
        if (hasNulls(pathComponents)) {
            String msg = String.format("%s: %s", 
                                       "some path components are null", 
                                       Arrays.toString(pathComponents));
            throw new IllegalArgumentException(msg);
        }
        
        String joined = Stream.of(pathComponents)
                              .map(Object::toString)
                              .filter(s -> !s.isEmpty())
                              .collect(joining("/"));
        
        if (joined.isEmpty()) {
            throw new IllegalArgumentException("all path components are empty");
        }
        
        return absolute ? "/" + joined : joined;
    }

    private final String location;
    
    private ResourceLocation(String scheme, Object...pathComponents) {
        this(scheme, true, pathComponents);
    }

    private ResourceLocation(String scheme, 
                             boolean absolutePath, 
                             Object...pathComponents) {
        location = build(scheme, absolutePath, pathComponents);
    }
    
    /**
     * Creates a new instance pointing to the specified resource.
     * @param location the resource's location.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public ResourceLocation(URI location) {
        requireNonNull(location, "location");
        this.location = location.toString();
    }
    
    @Override
    public String get() {
        return location;
    }
    
}

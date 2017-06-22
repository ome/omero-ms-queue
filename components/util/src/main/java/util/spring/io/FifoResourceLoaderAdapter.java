package util.spring.io;

import static java.util.Objects.requireNonNull;
import static util.sequence.Streams.pruneNull;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * Uses a given {@link ResourceLoader} to implement a "first-available" strategy
 * to determine from which location to load a resource.
 */
public class FifoResourceLoaderAdapter implements LociResourceLoader {

    private final ResourceLoader resourceLoader;
    
    /**
     * Creates a new instance to use the given loader.
     * @param resourceLoader the loader to use.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public FifoResourceLoaderAdapter(ResourceLoader resourceLoader) {
        requireNonNull(resourceLoader, "resourceLoader");
        
        this.resourceLoader = resourceLoader;
    }
    
    private Optional<Resource> select(String[] loci) {
        return Stream.of(loci)
                     .filter(l -> l != null && !l.isEmpty())
                     .map(this::getResource)
                     .filter(r -> r != null && r.exists() && r.isReadable())
                     .findFirst();
    }
    
    /**
     * Uses the underlying {@link ResourceLoader} to select the first available 
     * resource out of the possible locations.
     * In detail, this method will select the resource at {@code loci[k]} with
     * {@code k} the minimum index for which the following conditions are met:
     * <ul>
     *  <li>{@code loci[k]} is not null or empty</li>
     *  <li>the resource at {@code loci[k]} {@link Resource#exists() exists}
     *      and {@link Resource#isReadable() is readable}</li>
     * </ul>
     * If none of the resources at the given locations satisfy this condition
     * or no locations are given, then {@link Optional#empty() empty} is 
     * returned.
     */
    @Override
    public Optional<Resource> selectResource(ResourceLocation...loci) {
        String[] xs = pruneNull(loci).map(ResourceLocation::get)
                                     .toArray(String[]::new);
        return select(xs);
    }
    
    /**
     * Just forwards to the underlying {@link ResourceLoader}.
     */
    @Override
    public Resource getResource(String location) {
        return resourceLoader.getResource(location);
    }
    
    @Override
    public ClassLoader getClassLoader() {
        return resourceLoader.getClassLoader();
    }
    
}

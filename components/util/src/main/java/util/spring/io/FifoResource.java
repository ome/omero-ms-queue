package util.spring.io;

import java.util.stream.Stream;

import org.springframework.core.io.ResourceLoader;


/**
 * Converts resource data into objects using a "first-available" strategy to 
 * determine from which location to load resource data with an option to fall
 * back to a predefined stream of objects if no resource data can be fetched 
 * from the specified resource locations.
 * The functionality is provided by the {@link #read(ResourceLocation...) read} 
 * template method. Subclasses have to fill in a {@link #getResourceLoader() 
 * resource loader} and a {@link #getConverter() resource reader} to convert 
 * resource data into objects; optionally a subclass may provide {@link 
 * #getFallback() fall-back} objects.  
 */
public abstract class FifoResource<T> {

    /**
     * Subclasses implement this method to provide a {@link ResourceLoader} to
     * access resources when {@link #read(ResourceLocation...) reading}
     * configuration in.
     * @return the resource loader to use.
     */
    protected abstract ResourceLoader getResourceLoader();
    
    /**
     * Subclasses implement this method to provide a {@link ResourceReader} to
     * convert resource data when {@link #read(ResourceLocation...) reading} 
     * data in.
     * @return the resource reader to use.
     */
    protected abstract ResourceReader<T> getConverter();
    
    /**
     * Subclasses may override this method to provide objects that the {@link 
     * #read(ResourceLocation...) read} method will return if no resource data
     * can be found in all of the specified resource locations.
     * @return the fall-back objects to use.
     */
    public Stream<T> getFallback() {
        return Stream.empty();
    }

    /**
     * Reads objects from the first resource available out of the specified 
     * locations, optionally {@link #getFallback() falling back} to a given 
     * stream of objects if no resource is available.
     * @param loci where to look for resources.
     * @return the items read from the first available resource or the fall-back
     * items (if provided) as the case may be. The returned stream may be empty
     * (e.g. the resource stream holds no data, no fall-back items provided)
     * but will never be {@code null}.
     * @throws Exception if an error occurred while reading resource data.
     */
    public Stream<T> read(ResourceLocation...loci) throws Exception {
        ResourceLoader sourceLoader = getResourceLoader();
        ResourceReader<T> converter = getConverter(); 
        
        return new FifoResourceLoaderAdapter(sourceLoader)
                        .selectResource(loci)
                        .map(converter::readResource)
                        .orElse(getFallback());
    }
    
}

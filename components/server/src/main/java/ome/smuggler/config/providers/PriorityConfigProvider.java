package ome.smuggler.config.providers;

import static util.spring.io.ResourceLocation.classpath;

import java.net.URI;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;

import ome.smuggler.config.BaseConfigDir;
import util.config.ConfigProvider;
import util.spring.io.FifoResource;
import util.spring.io.ResourceLocation;


/**
 * Base class for {@link ConfigProvider} beans that prioritize where 
 * configuration is read from.
 * This class first attempts to read configuration items from a file named
 * {@code fn} in the {@link BaseConfigDir base configuration directory}, where
 * where {@code fn} is the value returned by the {@link #getConfigFileName() 
 * getConfigFileName} method; if no such file is found there, then an attempt 
 * is made to read the file from the class path at {@code /config/fn}. Failing 
 * that, if {@link #getFallback() fall-back} configuration has been specified, 
 * then those items are returned; otherwise an empty stream is returned, meaning
 * no configuration is available.  
 */
public abstract class PriorityConfigProvider<T> 
    extends FifoResource<T> implements ConfigProvider<T> {

    @Autowired
    private ResourceLoader resourceLoader;
    
    private ResourceLocation fileInBaseConfigDir() {
        URI file = new BaseConfigDir().resolve(getConfigFileName()).toUri();
        return new ResourceLocation(file);
    }

    @Override
    protected ResourceLoader getResourceLoader() {
        return resourceLoader;
    } 
    
    /**
     * Subclasses implement this method to specify the name of the configuration
     * file to read data from. 
     * @return the configuration file name, e.g. 'my.config'.
     */
    public abstract String getConfigFileName();
    
    /**
     * Reads all the objects in the configuration file.
     * @return all configured objects; the stream may be empty but never 
     * {@code null}.
     */
    @Override
    public Stream<T> readConfig() throws Exception {
        String fileName = getConfigFileName();
        ResourceLocation tryBaseConfigDirFirst = fileInBaseConfigDir();
        ResourceLocation thenClasspath = classpath("config", fileName);
        
        return read(tryBaseConfigDirFirst, thenClasspath);
    }

    /**
     * Reads configuration from the first available out of the specified 
     * locations, falling back to hard-coded configuration if no file is 
     * available.
     * @param loci configuration locations.
     * @return all configured objects.
     * @throws Exception if an error occurs.
     */
    public Stream<T> readConfig(ResourceLocation... loci)
            throws Exception {
        return read(loci);
    }
    
}

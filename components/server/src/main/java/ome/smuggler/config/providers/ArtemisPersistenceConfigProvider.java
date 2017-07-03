package ome.smuggler.config.providers;

import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import ome.smuggler.config.data.DefaultArtemisPersistenceConfig;
import ome.smuggler.config.items.ArtemisPersistenceConfig;
import util.spring.io.ResourceReader;


/**
 * The Artemis operational parameters.
 * This configuration is hard-coded as it is only used internally by the
 * import server.
 */
@Component
public class ArtemisPersistenceConfigProvider
    extends PriorityConfigProvider<ArtemisPersistenceConfig> {

    public static final String FileName = "artemis-persistence.yml";
    
    @Override
    protected ResourceReader<ArtemisPersistenceConfig> getConverter() {
        return new YmlResourceReader<>(ArtemisPersistenceConfig.class);
    }
    
    @Override 
    public Stream<ArtemisPersistenceConfig> getFallback() {
        return new DefaultArtemisPersistenceConfig().readConfig();
    }

    @Override
    public String getConfigFileName() {
        return FileName;
    }
    
}

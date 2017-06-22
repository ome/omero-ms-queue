package ome.smuggler.config.providers;

import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import ome.smuggler.config.data.DefaultHornetQPersistenceConfig;
import ome.smuggler.config.items.HornetQPersistenceConfig;
import util.spring.io.ResourceReader;


/**
 * The HornetQ operational parameters.
 * This configuration is hard-coded as it is only used internally by the
 * import server.
 */
@Component
public class HornetQPersistenceConfigProvider 
    extends PriorityConfigProvider<HornetQPersistenceConfig> {

    public static final String FileName = "hornetq-persistence.yml";
    
    @Override
    protected ResourceReader<HornetQPersistenceConfig> getConverter() {
        return new YmlResourceReader<>(HornetQPersistenceConfig.class);
    }
    
    @Override 
    public Stream<HornetQPersistenceConfig> getFallback() {
        return new DefaultHornetQPersistenceConfig().readConfig();
    }

    @Override
    public String getConfigFileName() {
        return FileName;
    }
    
}

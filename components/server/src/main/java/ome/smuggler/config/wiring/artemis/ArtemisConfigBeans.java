package ome.smuggler.config.wiring.artemis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ome.smuggler.config.items.ArtemisPersistenceConfig;
import util.config.ConfigProvider;

/**
 * Spring bean wiring of configuration items.
 */
@Configuration
public class ArtemisConfigBeans {

    @Bean
    public ArtemisPersistenceConfig artemisPersistenceConfig(
            ConfigProvider<ArtemisPersistenceConfig> src) {
        return src.first();
    }
    
}

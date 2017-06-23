package ome.smuggler.config.wiring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ome.smuggler.config.items.HornetQPersistenceConfig;
import util.config.ConfigProvider;

/**
 * Spring bean wiring of configuration items.
 */
@Configuration
public class HornetQConfigBeans {

    @Bean
    public HornetQPersistenceConfig hornetQPersistenceConfig(
            ConfigProvider<HornetQPersistenceConfig> src) {
        return src.first();
    }
    
}

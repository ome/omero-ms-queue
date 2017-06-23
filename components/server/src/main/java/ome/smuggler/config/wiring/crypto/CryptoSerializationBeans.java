package ome.smuggler.config.wiring.crypto;

import ome.smuggler.core.types.CryptoConfigSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring bean wiring configuration for serialization.
 */
@Configuration
public class CryptoSerializationBeans {

    @Bean
    public SerializationFactory serializationFactory(CryptoConfigSource cfg) {
        return new SerializationFactory(cfg);
    }

}

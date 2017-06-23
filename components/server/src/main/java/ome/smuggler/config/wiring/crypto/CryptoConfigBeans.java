package ome.smuggler.config.wiring.crypto;

import ome.smuggler.config.Profiles;
import ome.smuggler.config.items.CryptoConfig;
import ome.smuggler.core.types.CryptoConfigReader;
import ome.smuggler.core.types.CryptoConfigSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import util.config.ConfigProvider;
import util.config.ConfigReader;

/**
 * Spring bean wiring of crypto configuration items for the {@link Profiles#Prod
 * Prod} profile.
 */
@Configuration
@Profile(Profiles.Prod)
public class CryptoConfigBeans {

    @Bean
    public CryptoConfigSource cryptoConfig(ConfigProvider<CryptoConfig> src) {
        return ConfigReader.newReader(src, CryptoConfigReader::new).first();
    }

}

package ome.smuggler.config.wiring.crypto;


import ome.smuggler.config.Profiles;
import ome.smuggler.config.data.DevCryptoConfigSource;
import ome.smuggler.core.types.CryptoConfigSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Spring bean wiring of crypto configuration items for the {@link Profiles#Dev
 * Dev} profile.
 */
@Configuration
@Profile(Profiles.Dev)
public class CryptoDevConfigBeans {

    @Bean
    public CryptoConfigSource cryptoConfig() {
        return new DevCryptoConfigSource();
    }

}

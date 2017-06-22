package ome.smuggler.config.wiring.omero;

import ome.smuggler.config.Profiles;
import ome.smuggler.config.items.OmeCliConfig;
import ome.smuggler.config.items.OmeroSessionQConfig;
import ome.smuggler.core.types.OmeCliConfigReader;
import ome.smuggler.core.types.OmeCliConfigSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import util.config.ConfigProvider;
import util.config.ConfigReader;

/**
 * Spring bean wiring of OMERO configuration items for the {@link Profiles#Prod
 * Prod} profile.
 */
@Configuration
@Profile(Profiles.Prod)
public class OmeroConfigBeans {

    @Bean
    public OmeCliConfigSource omeCliConfig(ConfigProvider<OmeCliConfig> src) {
        return ConfigReader.newReader(src, OmeCliConfigReader::new).first();
    }

    @Bean
    public OmeroSessionQConfig omeroSessionQConfig(
            ConfigProvider<OmeroSessionQConfig> src) {
        return src.first();
    }

}

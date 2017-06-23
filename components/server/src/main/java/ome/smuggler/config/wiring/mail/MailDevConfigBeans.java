package ome.smuggler.config.wiring.mail;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import ome.smuggler.config.Profiles;
import ome.smuggler.config.data.DevMailConfigSource;
import ome.smuggler.config.items.MailQConfig;
import ome.smuggler.core.types.MailConfigSource;
import util.config.ConfigProvider;

/**
 * Spring bean wiring of configuration items for the {@link Profiles#Dev Dev}
 * profile.
 */
@Configuration
@Profile(Profiles.Dev)
public class MailDevConfigBeans {

    @Bean
    public MailQConfig mailQConfig(ConfigProvider<MailQConfig> src) {
        return src.first();
    }
    
    @Bean
    public MailConfigSource mailConfig() {
        return new DevMailConfigSource();
    }
    
}

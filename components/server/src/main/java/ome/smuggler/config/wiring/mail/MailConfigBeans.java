package ome.smuggler.config.wiring.mail;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import ome.smuggler.config.Profiles;
import ome.smuggler.config.items.MailConfig;
import ome.smuggler.config.items.MailQConfig;
import ome.smuggler.core.types.MailConfigReader;
import ome.smuggler.core.types.MailConfigSource;
import util.config.ConfigProvider;
import util.config.ConfigReader;

/**
 * Spring bean wiring of configuration items.
 */
@Configuration
@Profile(Profiles.Prod)
public class MailConfigBeans {

    @Bean
    public MailQConfig mailQConfig(ConfigProvider<MailQConfig> src) {
        return src.first();
    }
    
    @Bean
    public MailConfigSource mailConfig(ConfigProvider<MailConfig> src) {
        return ConfigReader.newReader(src, MailConfigReader::new).first();
    }
    
}

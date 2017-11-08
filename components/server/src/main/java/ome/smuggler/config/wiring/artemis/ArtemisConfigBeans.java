package ome.smuggler.config.wiring.artemis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kew.providers.artemis.runtime.DeploymentSpec;
import ome.smuggler.config.items.*;
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

    @Bean
    public DeploymentSpec artemisDeploymentSpec(
            ArtemisPersistenceConfig persistenceConfig,
            ImportQConfig importQ,
            ImportGcQConfig importGcQ,
            MailQConfig mailQ,
            OmeroSessionQConfig omeroSessionQ) {
        return new ArtemisConfigurator(persistenceConfig,
                                       importQ, importGcQ, mailQ, omeroSessionQ)
              .buildDeploymentSpec();
    }

}

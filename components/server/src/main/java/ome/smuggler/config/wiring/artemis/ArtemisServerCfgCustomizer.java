package ome.smuggler.config.wiring.artemis;

import static kew.providers.artemis.config.QueueConfig.q;
import static kew.providers.artemis.config.StorageProps.defaultStorageSettings;
import static kew.providers.artemis.config.StorageProps.persistenceEnabled;
import static kew.providers.artemis.config.security.SecurityProps.securityEnabled;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.activemq.artemis.core.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisConfigurationCustomizer;
import org.springframework.stereotype.Component;

import ome.smuggler.config.items.*;
import util.object.Builder;

/**
 * Implements the Spring Boot auto-configuration hook to customize the Artemis
 * server configuration created by Spring Boot.
 */
@Component
public class ArtemisServerCfgCustomizer
        implements ArtemisConfigurationCustomizer {

    @Autowired
    private ArtemisPersistenceConfig params;
    
    @Autowired
    private ImportQConfig importQ;
    
    @Autowired
    private ImportGcQConfig importGcQ;

    @Autowired
    private MailQConfig mailQ;

    @Autowired
    private OmeroSessionQConfig omeroSessionQ;

    private Builder<Void, Configuration> configBuilder(Configuration cfg) {
        Path dataDir = Paths.get(params.getDataDirPath());
        boolean enablePersistence = params.isPersistenceEnabled();

        return Builder.make(() -> cfg)
                      .with(securityEnabled(false))
                      .with(defaultStorageSettings(dataDir))
                      .with(persistenceEnabled(enablePersistence))
                      .with(q(importQ, importGcQ, mailQ, omeroSessionQ));
    }
    
    @Override
    public void customize(Configuration cfg) {
        configBuilder(cfg).apply(null);
    }

}
/* NOTES.
 * 1. Artemis server configuration.
 * The customize method is passed an Artemis Configuration instance created with
 * the values in ArtemisProperties. For the details, see the source code of:
 * 
 *  - ArtemisEmbeddedServerConfiguration
 *  - ArtemisEmbeddedConfigurationFactory
 *
 * (package: org.springframework.boot.autoconfigure.jms.artemis)
 *
 * Artemis Configuration is created by Spring Boot pretty much as shown in the
 * Artemis EmbeddedExample class.
 * 
 * 2. Performance.
 * The Artemis docs (Troubleshooting and Performance Tuning / Avoiding
 * Anti-Patterns) state that connections, sessions, consumers, and producers
 * are supposed to be shared, but the Spring JMS template does not. So you
 * shouldn't use it with Artemis...
 */

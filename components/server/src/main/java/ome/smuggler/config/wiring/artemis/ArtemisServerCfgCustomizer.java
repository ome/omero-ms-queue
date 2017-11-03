package ome.smuggler.config.wiring.artemis;

import static util.sequence.Arrayz.asMutableList;

import ome.smuggler.config.items.*;

import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.server.JournalType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisConfigurationCustomizer;
import org.springframework.stereotype.Component;


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
    
    private void configurePersistence(Configuration cfg) {
        cfg.setPersistenceEnabled(params.isPersistenceEnabled());
        cfg.setJournalType(JournalType.NIO);
        cfg.setJournalDirectory(params.getJournalDirPath());
        cfg.setLargeMessagesDirectory(params.getLargeMessagesDirPath());
        cfg.setBindingsDirectory(params.getBindingsDirPath());
        cfg.setPagingDirectory(params.getPagingDirPath());
    }
    
    private void configureQueues(Configuration cfg) {
        cfg.setQueueConfigurations(
                asMutableList(importQ, importGcQ, mailQ, omeroSessionQ));
    }
    
    @Override
    public void customize(Configuration cfg) {
        configurePersistence(cfg);
        configureQueues(cfg);
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

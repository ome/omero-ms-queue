package ome.smuggler.config.wiring.artemis;

import static java.util.Objects.requireNonNull;
import static kew.providers.artemis.config.QueueConfig.q;
import static kew.providers.artemis.config.StorageProps.defaultStorageSettings;
import static kew.providers.artemis.config.StorageProps.persistenceEnabled;
import static kew.providers.artemis.config.security.SecurityManagerProps.defaultSecurityManager;
import static kew.providers.artemis.config.security.SecurityProps.securityEnabled;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.config.CoreQueueConfiguration;

import kew.providers.artemis.config.CoreConfigFactory;
import kew.providers.artemis.runtime.DeploymentSpec;
import ome.smuggler.config.items.*;
import util.object.Builder;

/**
 * Helper class to build a deployment spec for the embedded Artemis server.
 */
public class ArtemisConfigurator {

    private final ArtemisPersistenceConfig persistenceConfig;
    private final CoreQueueConfiguration[] queues;

    /**
     * Creates a new instance.
     * @param persistenceConfig our Artemis persistence settings.
     * @param queues the queues to deploy.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public ArtemisConfigurator(ArtemisPersistenceConfig persistenceConfig,
                               CoreQueueConfiguration...queues) {
        requireNonNull(persistenceConfig, "persistenceConfig");
        requireNonNull(queues, "queues");

        this.persistenceConfig = persistenceConfig;
        this.queues = queues;
    }

    private Builder<Void, Configuration> configBuilder() {
        Path dataDir = Paths.get(persistenceConfig.getDataDirPath());
        boolean enablePersistence = persistenceConfig.isPersistenceEnabled();

        return CoreConfigFactory
              .empty()
              .with(securityEnabled(false))
              .with(defaultStorageSettings(dataDir))
              .with(persistenceEnabled(enablePersistence))
              .with(q(queues));
    }

    /**
     * Builds the deployment spec for the embedded Artemis server.
     * @return the spec.
     */
    public DeploymentSpec buildDeploymentSpec() {
        return new DeploymentSpec(configBuilder(),
                                  defaultSecurityManager(),
                                  Optional.empty());
    }

}

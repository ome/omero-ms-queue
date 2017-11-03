package kew.providers.artemis.config;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.config.CoreQueueConfiguration;

/**
 * Type-safe configuration properties for queues in the underlying Artemis core
 * configuration. Use them with a configuration builder.
 * @see CoreConfigFactory
 */
public class QueueConfig {

    /**
     * A setter to add a queue configuration to the core configuration.
     * @param configSupplier supplies the queue configuration.
     * @return the setter.
     * @throws NullPointerException if the argument is {@code null} or the
     * supplied queue configuration is {@code null}.
     */
    public static Function<Configuration, Configuration> q(
            Supplier<CoreQueueConfiguration> configSupplier) {
        Supplier<NullPointerException> error = () ->
                new NullPointerException("no queue configuration supplied.");
        CoreQueueConfiguration qConfig = Optional.ofNullable(configSupplier)
                                                 .map(Supplier::get)
                                                 .orElseThrow(error);

        return cfg -> cfg.addQueueConfiguration(qConfig);
    }

    /**
     * A setter to add a queue configuration to the core configuration.
     * @param qConfig the queue configuration.
     * @return the setter.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static Function<Configuration, Configuration> q(
            CoreQueueConfiguration qConfig) {
        return q(() -> qConfig);
    }

}
/* NOTE. More type-safety.
 * We could add more checks here to make sure queue names don't use Artemis
 * FQN separator '::', address names are unique, etc. Well, if I only had
 * the time...
 */
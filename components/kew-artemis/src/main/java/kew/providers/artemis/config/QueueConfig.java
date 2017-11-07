package kew.providers.artemis.config;

import static util.sequence.Arrayz.requireArray;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

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
     * A setter to add queue configurations to the core configuration.
     * @param qConfig the queue(s) to deploy.
     * @return the setter.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static Function<Configuration, Configuration> q(
            CoreQueueConfiguration...qConfig) {
        requireArray(qConfig);
        return cfg -> Stream.of(qConfig)
                            .map(cfg::addQueueConfiguration)
                            .reduce(cfg, (x, y) -> x);
    }

}
/* NOTE. More type-safety.
 * We could add more checks here to make sure queue names don't use Artemis
 * FQN separator '::', address names are unique, etc. Well, if I only had
 * the time...
 */
package kew.providers.artemis.config;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.util.function.Function;

import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.server.JournalType;

/**
 * Type-safe configuration properties for the data storage parameters of an
 * underlying Artemis core configuration. Use them with a configuration builder.
 * @see CoreConfigFactory
 */
public class StorageProps {

    /**
     * A setter for commonly used storage settings.
     * Specifically, it configures Artemis to create the storage directories
     * under a specified root data directory and use persistence with a NIO
     * journal.
     * @param dataDir the root data directory.
     * @return the setter.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static Function<Configuration, Configuration>
    defaultStorageSettings(Path dataDir) {
        return storageOn(dataDir).andThen(persistence(JournalType.NIO));
    }

    /**
     * A setter that enables queues being persisted to a journal of the
     * specified type.
     * @param t the journal type.
     * @return the setter.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static Function<Configuration, Configuration>
    persistence(JournalType t) {
        requireNonNull(t, "t");
        return cfg -> cfg.setPersistenceEnabled(true)
                         .setJournalType(t);
    }

    /**
     * A setter that configures Artemis to create various storage directories
     * under a specified root data directory.
     * Specifically, we tell Artemis to use journal, large-messages, bindings,
     * and paging directories under the specified data directory. Artemis will
     * create sub-directories as needed.
     * @param dataDir the root data directory.
     * @return the setter.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static Function<Configuration, Configuration>
    storageOn(Path dataDir) {
        requireNonNull(dataDir, "dataDir");
        Function<String, String> dir = d -> dataDir.resolve(d).toString();
        return cfg -> cfg.setJournalDirectory(dir.apply("journal"))
                         .setLargeMessagesDirectory(dir.apply("largemessages"))
                         .setBindingsDirectory(dir.apply("bindings"))
                         .setPagingDirectory(dir.apply("paging"));
    }

}

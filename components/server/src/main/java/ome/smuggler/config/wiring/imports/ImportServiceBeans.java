package ome.smuggler.config.wiring.imports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kew.core.msg.ChannelSource;
import kew.core.msg.SchedulingSource;
import ome.smuggler.core.service.file.KeyValueStore;
import ome.smuggler.core.service.file.TaskFileStore;
import ome.smuggler.core.service.file.impl.KeyValueFileStore;
import ome.smuggler.core.service.file.impl.TSafeKeyValueStore;
import ome.smuggler.core.service.file.impl.TaskIdPathStore;
import ome.smuggler.core.service.imports.*;
import ome.smuggler.core.service.imports.impl.*;
import ome.smuggler.core.service.mail.MailRequestor;
import ome.smuggler.core.service.omero.ImportService;
import ome.smuggler.core.service.omero.SessionService;
import ome.smuggler.core.types.*;
import ome.smuggler.providers.log.LogAdapter;
import util.serialization.SerializationFactory;

/**
 * Spring bean wiring configuration for the import service interfaces.
 */
@Configuration
public class ImportServiceBeans {

    @Autowired
    private SerializationFactory sf;

    @Bean
    public TaskFileStore<ImportId> failedImportLogStore(
            ImportConfigSource config) {
        return new TaskIdPathStore<>(config.failedImportLogDir(), 
                                     ImportId::new);
    }

    @Bean
    public KeyValueStore<ImportBatchId, ImportBatchStatus> batchStore(
            ImportConfigSource config) {
        TaskFileStore<ImportBatchId> backingStore = new TaskIdPathStore<>(
                config.batchStatusDbDir(),
                ImportBatchId::new);
        KeyValueStore<ImportBatchId, ImportBatchStatus> store =
                new KeyValueFileStore<>(
                        backingStore,
                        sf.deserializer(ImportBatchStatus.class),
                        sf.serializer());
        return new TSafeKeyValueStore<>(store,
                                        config.batchStatusDbLockStripes());
    }

    @Bean
    public ImportEnv importEnv(
            ImportConfigSource config,
            SessionService session,
            ImportService importer,
            ChannelSource<QueuedImport> importSourceChannel,
            SchedulingSource<ProcessedImport> importGcSourceChannel,
            KeyValueStore<ImportBatchId, ImportBatchStatus> batchStore,
            TaskFileStore<ImportId> failedImportLogStore,
            MailRequestor mail, 
            MailConfigSource mailConfig) {
        ImportEnv env = new ImportEnv(config, session, importer,
                                      importSourceChannel,
                                      importGcSourceChannel,
                                      batchStore,
                                      failedImportLogStore, mail, 
                                      mailConfig.sysAdminAddress(), 
                                      new LogAdapter());
        env.ensureDirectories();
        return env;
    }

    @Bean
    public ImportRequestor importRequestor(ImportEnv env) {
        return new ImportTrigger(env);
    }

    @Bean
    public ImportProcessor importProcessor(ImportEnv env) {
        return new ImportRunner(env);
    }

    @Bean
    public FailedImportHandler failedImportHandler(ImportEnv env) {
        return new ImportFailureHandler(env);
    }

    @Bean
    public ImportTracker importTracker(ImportEnv env) {
        return new ImportMonitor(env);
    }

    @Bean
    public ImportFinaliser importFinaliser(ImportEnv env) {
        return new Finaliser(env);
    }

    @Bean
    public FailedFinalisationHandler failedImportFinalisationHandler(
            ImportEnv env) {
        return new FinaliserFailureHandler(env);
    }

}

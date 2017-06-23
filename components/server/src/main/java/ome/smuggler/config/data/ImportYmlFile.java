package ome.smuggler.config.data;

import java.time.Duration;
import java.util.stream.Stream;

import ome.smuggler.config.items.ImportConfig;
import util.config.ConfigProvider;


/**
 * Default configuration for the import, i.e. the content of the YAML file
 * if provided.
 */
public class ImportYmlFile implements ConfigProvider<ImportConfig> {

    public static final String RelImportLogDirPath = "import/log";
    public static final String RelFailedImportLogDirPath = "import/failed-log";
    public static final String RelBatchStatusDbDirPath = "import/db/batch-status";
    
    @Override
    public Stream<ImportConfig> readConfig() {
        ImportConfig cfg = new ImportConfig();
        cfg.setImportLogDir(RelImportLogDirPath);
        cfg.setFailedImportLogDir(RelFailedImportLogDirPath);
        cfg.setLogRetentionMinutes(Duration.ofDays(15).toMinutes());
        cfg.setRetryIntervals(new Long[] { 10L, 10L, 120L, 1440L, 1440L, 1440L });
        cfg.setBatchStatusDbDir(RelBatchStatusDbDirPath);

        return Stream.of(cfg);
    }
    
}

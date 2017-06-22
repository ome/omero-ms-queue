package ome.smuggler.config.data;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

import ome.smuggler.config.BaseDataDir;
import ome.smuggler.core.types.ImportConfigSource;
import ome.smuggler.core.types.PositiveN;
import util.runtime.CommandBuilder;

/**
 * Dev import settings.
 */
public class DevImportConfigSource implements ImportConfigSource {

    private final BaseDataDir baseDataDir;
    
    public DevImportConfigSource() {
        this.baseDataDir = new BaseDataDir();
    }
    
    @Override
    public Path importLogDir() {
        return baseDataDir.resolve(ImportYmlFile.RelImportLogDirPath);
    }

    @Override
    public Duration logRetentionPeriod() {
        return Duration.ofSeconds(5);
    }

    @Override
    public List<Duration> retryIntervals() {
        return Collections.emptyList();
    }

    @Override
    public Path failedImportLogDir() {
        return baseDataDir.resolve(ImportYmlFile.RelFailedImportLogDirPath);
    }

    @Override
    public CommandBuilder niceCommand() {
        return CommandBuilder.empty();
    }

    @Override
    public Path batchStatusDbDir() {
        return baseDataDir.resolve(ImportYmlFile.RelBatchStatusDbDirPath);
    }

    @Override
    public PositiveN batchStatusDbLockStripes() {
        return DefaultBatchStatusDbLockStripes;
    }

}

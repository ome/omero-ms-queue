package ome.smuggler.config.data;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.util.stream.Stream;

import ome.smuggler.config.BaseDataDir;
import ome.smuggler.config.items.HornetQPersistenceConfig;
import util.config.ConfigProvider;

/**
 * Hard-coded operational parameters for the embedded HornetQ server.
 */
public class DefaultHornetQPersistenceConfig 
    implements ConfigProvider<HornetQPersistenceConfig> {

    public static final String RootDir = "hornetq";

    private static HornetQPersistenceConfig build(BaseDataDir baseDir) {
        requireNonNull(baseDir, "baseDir");
        
        Path d = baseDir.resolve(RootDir);
        HornetQPersistenceConfig cfg = new HornetQPersistenceConfig();
        
        cfg.setPersistenceEnabled(true);
        cfg.setBindingsDirPath(d.resolve("bindings").toString());
        cfg.setJournalDirPath(d.resolve("journal").toString());
        cfg.setLargeMessagesDirPath(d.resolve("largemessages").toString());
        cfg.setPagingDirPath(d.resolve("paging").toString());
        
        return cfg;
    }

    @Override
    public Stream<HornetQPersistenceConfig> readConfig() {
        BaseDataDir baseDir = new BaseDataDir();
        return Stream.of(build(baseDir));
    }

}

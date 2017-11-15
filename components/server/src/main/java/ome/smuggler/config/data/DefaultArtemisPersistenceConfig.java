package ome.smuggler.config.data;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.util.stream.Stream;

import ome.smuggler.config.BaseDataDir;
import ome.smuggler.config.items.ArtemisPersistenceConfig;
import util.config.ConfigProvider;

/**
 * Hard-coded operational parameters for the embedded Artemis server.
 */
public class DefaultArtemisPersistenceConfig
    implements ConfigProvider<ArtemisPersistenceConfig> {

    public static final String RootDir = "artemis";

    private static ArtemisPersistenceConfig build(BaseDataDir baseDir) {
        requireNonNull(baseDir, "baseDir");
        
        Path d = baseDir.resolve(RootDir);
        ArtemisPersistenceConfig cfg = new ArtemisPersistenceConfig();

        cfg.setPersistenceEnabled(true);
        cfg.setDataDirPath(d.toString());

        return cfg;
    }

    @Override
    public Stream<ArtemisPersistenceConfig> readConfig() {
        BaseDataDir baseDir = new BaseDataDir();
        return Stream.of(build(baseDir));
    }

}

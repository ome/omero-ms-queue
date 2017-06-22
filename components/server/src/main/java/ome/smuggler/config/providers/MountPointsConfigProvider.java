package ome.smuggler.config.providers;

import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import ome.smuggler.config.data.MountPointsYmlFile;
import ome.smuggler.config.items.MountPointsConfig;
import util.spring.io.ResourceReader;

/**
 * Reads mount points configuration from a YAML file, falling back to hard-coded
 * configuration if no file is available.
 * This provider will first try to read the file from the current directory;
 * failing that, it will try to find the file in the class-path, falling back
 * to hard-coded config if not found.
 */
@Component
public class MountPointsConfigProvider
        extends PriorityConfigProvider<MountPointsConfig> {

    public static final String FileName = "mount-points.yml";

    @Override
    protected ResourceReader<MountPointsConfig> getConverter() {
        return new YmlResourceReader<>(MountPointsConfig.class);
    }

    @Override
    public Stream<MountPointsConfig> getFallback() {
        return new MountPointsYmlFile().readConfig();
    }

    @Override
    public String getConfigFileName() {
        return FileName;
    }

}

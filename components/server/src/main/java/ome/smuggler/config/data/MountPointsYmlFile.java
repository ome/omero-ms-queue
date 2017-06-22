package ome.smuggler.config.data;

import java.util.stream.Stream;

import ome.smuggler.config.items.MountPointsConfig;
import ome.smuggler.config.items.MountPointsConfig.RemoteToLocalMapping;
import util.config.ConfigProvider;

/**
 * Default configuration for mount points mapping, i.e. the content of the YAML
 * file if not explicitly provided.
 */
public class MountPointsYmlFile implements ConfigProvider<MountPointsConfig> {

    @Override
    public Stream<MountPointsConfig> readConfig() {
        MountPointsConfig cfg = new MountPointsConfig();
        cfg.setEnableTranslation(false);

        // with translation off, Smuggler will ignore the example config below.
        RemoteToLocalMapping m1 = new RemoteToLocalMapping();
        m1.setRemoteBaseUri("file://host1/data");
        m1.setLocalBasePath("/mnt/d1");
        RemoteToLocalMapping m2 = new RemoteToLocalMapping();
        m2.setRemoteBaseUri("file://host2/data");
        m2.setLocalBasePath("/mnt/d2");

        cfg.setRemoteToLocalMap(new RemoteToLocalMapping[] { m1, m2 });

        return Stream.of(cfg);
    }

}

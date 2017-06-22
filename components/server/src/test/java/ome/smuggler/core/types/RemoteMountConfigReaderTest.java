package ome.smuggler.core.types;


import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import ome.smuggler.config.data.MountPointsYmlFile;
import ome.smuggler.config.items.MountPointsConfig;
import org.junit.Test;

import java.util.List;
import java.util.function.Consumer;

public class RemoteMountConfigReaderTest {

    private static MountPointsConfig validConfig() {
        return new MountPointsYmlFile().first();
    }

    private static RemoteMountConfigSource reader(
            Consumer<MountPointsConfig> tweak) {
        MountPointsConfig config = validConfig();
        tweak.accept(config);
        return new RemoteMountConfigReader(config);
    }

    private static List<RemoteMount> assertHasRemoteToLocalMap(
            RemoteMountConfigSource target) {
        List<RemoteMount> map = target.remoteToLocalMap();
        assertNotNull(map);
        return map;
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullConfig() {
        new RemoteMountConfigReader(null);
    }

    @Test
    public void filterOutNullMappings() {
        RemoteMountConfigSource target = reader(
                cfg -> cfg.getRemoteToLocalMap()[0] = null);
        List<RemoteMount> mounts = assertHasRemoteToLocalMap(target);
        assertThat(mounts.size(), is(1));
    }

    @Test
    public void validConfigWithTranslationOff() {
        RemoteMountConfigSource target = reader(cfg -> {});

        assertFalse(target.enableTranslation());

        List<RemoteMount> mounts = assertHasRemoteToLocalMap(target);
        assertThat(mounts.size(), is(2));
    }

}

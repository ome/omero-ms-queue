package ome.smuggler.core.types;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static util.sequence.Streams.pruneNull;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import ome.smuggler.config.items.MountPointsConfig;
import ome.smuggler.config.items.MountPointsConfig.RemoteToLocalMapping;

/**
 * Implements {@link RemoteMountConfigSource} by extracting and validating
 * values from an underlying {@link MountPointsConfig}.
 */
public class RemoteMountConfigReader implements RemoteMountConfigSource {

    private static RemoteMount toRemoteMount(RemoteToLocalMapping m) {
        URI remoteBase = URI.create(m.getRemoteBaseUri());
        Path localBase = Paths.get(m.getLocalBasePath());
        return new RemoteMount(remoteBase, localBase);
    }

    private static List<RemoteMount> listMounts(MountPointsConfig config) {
        return pruneNull(config.getRemoteToLocalMap())
              .map(RemoteMountConfigReader::toRemoteMount)
              .collect(toList());
    }


    private final boolean enableTranslation;
    private final List<RemoteMount> remoteToLocalMap;

    /**
     * Creates a new instance.
     * @param config the configuration as obtained by the configuration
     * provider.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public RemoteMountConfigReader(MountPointsConfig config) {
        requireNonNull(config, "config");

        this.enableTranslation = config.isEnableTranslation();
        this.remoteToLocalMap = listMounts(config);
    }

    @Override
    public boolean enableTranslation() {
        return enableTranslation;
    }

    @Override
    public List<RemoteMount> remoteToLocalMap() {
        return remoteToLocalMap;
    }

}

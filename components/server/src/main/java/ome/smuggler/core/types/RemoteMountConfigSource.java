package ome.smuggler.core.types;

import java.util.List;

/**
 * Provides read-only, type-safe access to remote mount point configuration.
 * @see ome.smuggler.config.items.MountPointsConfig
 * @see RemoteMount
 */
public interface RemoteMountConfigSource {

    /**
     * @return whether to enable remote-to-local path translation.
     */
    boolean enableTranslation();

    /**
     * Lists the configured remote-to-local mappings. If the underlying list
     * is {@code null}, then an empty list is returned. Also, any {@code null}
     * elements are filtered out.
     * @return the configured remote-to-local mappings.
     */
    List<RemoteMount> remoteToLocalMap();

}

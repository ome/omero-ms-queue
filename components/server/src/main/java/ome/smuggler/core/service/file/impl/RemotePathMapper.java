package ome.smuggler.core.service.file.impl;

import static java.util.Objects.requireNonNull;

import ome.smuggler.core.service.file.RemotePathResolver;
import ome.smuggler.core.types.RemoteMount;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;


/**
 * Implements {@link RemotePathResolver} using a configured remote-to-local
 * map.
 */
public class RemotePathMapper implements RemotePathResolver {

    private final List<RemoteMount> remoteToLocalMap;

    /**
     * Creates a new instance.
     * @param remoteToLocalMap remote-to-local base path mappings.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public RemotePathMapper(List<RemoteMount> remoteToLocalMap) {
        requireNonNull(remoteToLocalMap, "remoteToLocalMap");
        this.remoteToLocalMap = remoteToLocalMap;
    }

    @Override
    public Optional<Path> toLocalPath(URI remotePath) {
        requireNonNull(remotePath, "remotePath");

        return remoteToLocalMap.stream()
              .map(remoteMount -> remoteMount.toLocalPath(remotePath))
              .filter(Optional::isPresent)
              .map(Optional::get)
              .findFirst();
    }

    @Override
    public Path forceLocalPath(URI path) {
        return toLocalPath(path).orElseGet(() -> Paths.get(path));
    }

}
/* TODO all this can be improved big time!
 * For example, consider an implementation that uses tries or radix sort.
 */
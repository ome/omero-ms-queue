package ome.smuggler.core.service.file;

import java.net.URI;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Remote-to-local path translation.
 */
public interface RemotePathResolver {

    /**
     * Resolves a remote path into a local one.
     * The input remote file path is mapped to a local file as documented in
     * {@link ome.smuggler.core.types.RemoteMount}. If no remote-to-local
     * mappings have been configured or none of them can be used to resolve
     * the remote path, then empty is returned.
     *
     * @param remotePath a file URI pointing to a file on a remote device
     *                   connected to this machine through a suitable mount
     *                   point.
     * @return the mapped path.
     * @throws NullPointerException if the argument is {@code null}.
     */
    Optional<Path> toLocalPath(URI remotePath);

    /**
     * Same as {@link #toLocalPath(URI) toLocalPath}, but forcibly converts
     * the input into a local path instead of returning empty when it can't
     * be resolved using remote-to-local mappings.
     * Here "forcibly" means we blindly assume the URI is that of a local
     * file that the {@link java.nio.file.Paths} can convert to a {@link Path}.
     *
     * @param path a file URI pointing to a file on a remote device connected
     *            to this machine through a suitable mount point or a file URI
     *            of a local file.
     * @return the mapped path.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws IllegalArgumentException if the argument is not a file URI
     * (i.e. doesn't have a "file" scheme) or it is a remote file URI (i.e.
     * has a host component) but can't be resolved to a local file using
     * remote-to-local mappings.
     */
    Path forceLocalPath(URI path);

}

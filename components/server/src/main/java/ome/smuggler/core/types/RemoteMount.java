package ome.smuggler.core.types;

import static java.util.Objects.requireNonNull;
import static util.string.Strings.isNullOrEmpty;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;


/**
 * Provides a way to translate a file path on a remote device into a local
 * path. The idea is that if you have a remote device mounted locally and
 * a client on the remote device sends you a path as it sees it there, you
 * can turn that path into a local one provided you have some way to rebase
 * the remote path at your local mount point.
 *
 * In detail, path resolution happens by means of a given mapping of a base
 * remote directory {@code RB} into a local base directory {@code LB}. Here,
 * {@code RB} is a file URI having host and path components, whereas {@code LB}
 * is a local path. Now take a file URI with a prefix of {@code RB}, {@code f
 * = RB + p} where {@code p} is the path following {@code RB} in the URI.
 * Then {@code f} corresponds to the local path of {@code LB + p}. For example,
 * if {@code RB = file://host-1/data/} and {@code LB = /mnt/h1/data}, then
 * a URI of {@code file://host-1/data/some/file} gets translated to a local
 * path of {@code /mnt/h1/data/some/file}, but a URI of {@code
 * file://host-1/dataxxx/some/file} doesn't get translated because it doesn't
 * have a prefix of {@code RB}.
 */
public class RemoteMount {

    /**
     * Is the given URI that of a file path on a remote host?
     *
     * @param x the URI to test.
     * @return {@code true} just in case the URI is not {@code null}, has a
     * "file" scheme and a host part too.
     */
    public static boolean isRemoteFilePath(URI x) {
        return x != null
                && "file".equals(x.getScheme())
                && !isNullOrEmpty(x.getHost());
    }


    private final URI remoteBasePath;
    private final Path localBasePath;

    /**
     * Creates a new instance.
     *
     * @param remoteBasePath the remote base path.
     * @param localBasePath the local base path. If not absolute, we make it
     * such. So in general you should probably pass in an absolute path!
     * @throws NullPointerException if any argument is {@code null}.
     * @throws IllegalArgumentException if the remote base path is not that of
     * a {@link #isRemoteFilePath(URI) remote file path}.
     */
    public RemoteMount(URI remoteBasePath, Path localBasePath) {
        requireNonNull(remoteBasePath, "remoteBasePath");
        requireNonNull(localBasePath, "localBasePath");
        if (!isRemoteFilePath(remoteBasePath)) {
            throw new IllegalArgumentException(
                String.format("not a remote file path: %s", remoteBasePath));
        }

        this.remoteBasePath = remoteBasePath;
        this.localBasePath = localBasePath.toAbsolutePath();
    }

    private Optional<String> ensureRemote(URI p) {
        return isRemoteFilePath(p) ? Optional.of(p.toString())
                                   : Optional.empty();
    }

    private Optional<String> extractRelativePath(String remotePath) {
        String base = remoteBasePath.toString();
        if (!base.endsWith("/")) {
            base = base + "/";
        }
        if (remotePath.startsWith(base)) {
            String relPath = remotePath.substring(base.length());
            return Optional.of(relPath);
        }
        return Optional.empty();
    }

    private URI rebase(String relRemotePath) {  // (1)
        String base = localBasePath.toUri().getPath();
        if (!base.endsWith("/")) {              // (1)
            base = base + "/";
        }
        return URI.create("file://" + base + relRemotePath);
    }
    /* NOTES
     * 1. Path manipulation. We only use URI paths, so we know the separator
     * is '/'.
     * 2. URI.resolve method. I thought I could implement rebase() with a
     * simple one-liner:
     *
     *     return localBasePath.toUri().resolve(relRemotePath)
     *
     * but resolve() doesn't do what I thought I'd do!
     */

    /**
     * Tries to translate a remote path into a local one using the remote and
     * local base paths of this instance.
     * @param remotePath the remote path to translate.
     * @return the absolute path to a local file of empty if the remote path
     * couldn't be translated.
     */
    public Optional<Path> toLocalPath(URI remotePath) {
        return ensureRemote(remotePath)
              .flatMap(this::extractRelativePath)
              .map(this::rebase)
              .map(Paths::get);
    }

    @Override
    public String toString() {
        return String.format("%s | %s ", remoteBasePath, localBasePath);
    }

}
/* TODO all this can be improved big time!
 * For example, consider an implementation that uses tries or radix sort.
 */
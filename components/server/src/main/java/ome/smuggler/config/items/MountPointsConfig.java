package ome.smuggler.config.items;

import java.util.Arrays;
import java.util.Objects;

/**
 * Specifies how to configure remote-to-local translation file paths.
 * For the details of how this works, refer to the documentation of
 * {@link ome.smuggler.core.types.RemoteMount RemoteMount}.
 * The available settings:
 * <ul>
 *  <li>{@link #setEnableTranslation(boolean) Enabling of path translation}.
 *  If {@code true}, the {@link #setRemoteToLocalMap(RemoteToLocalMapping[])
 *  given remote-to-local map} will be used to translate remote file paths.
 *  If {@code false}, no translation takes place and any given
 *  {@link #setRemoteToLocalMap(RemoteToLocalMapping[]) mapping} will be
 *  ignored.
 *  </li>
 *  <li>{@link #setRemoteToLocalMap(RemoteToLocalMapping[]) Remote-to-local
 *  map}.
 *  Each {@link RemoteToLocalMapping entry} specifies how to translate a
 *  remote path into a local one. If this array is {@code null} or empty,
 *  no translation takes place.
 *  </li>
 * </ul>
 */
public class MountPointsConfig {
    // NB this has to be a Java Bean (i.e. getters/setters, no args ctor) to
    // be (de-)serialized painlessly by SnakeYaml.

    /**
     * Specifies how to map a remote file path into a local one.
     * The available settings:
     * <ul>
     *   <li>{@link #setRemoteBaseUri(String) Remote base path.}
     *   A <a href="https://en.wikipedia.org/wiki/File_URI_scheme">file URI</a>
     *   specifying a host and a directory path on that host.
     *   </li>
     *   <li>{@link #setLocalBasePath(String) Local base path.}
     *   The local directory path to use to rebase remote paths having a
     *   prefix equal to the {@link #setRemoteBaseUri(String) remote base
     *   path.}
     *   </li>
     * </ul>
     */
    public static class RemoteToLocalMapping {
        // NB this has to be a Java Bean (i.e. getters/setters, no args ctor) to
        // be (de-)serialized painlessly by SnakeYaml.

        private String remoteBaseUri;
        private String localBasePath;

        public String getRemoteBaseUri() {
            return remoteBaseUri;
        }

        public void setRemoteBaseUri(String remoteBaseUri) {
            this.remoteBaseUri = remoteBaseUri;
        }

        public String getLocalBasePath() {
            return localBasePath;
        }

        public void setLocalBasePath(String localBasePath) {
            this.localBasePath = localBasePath;
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other instanceof RemoteToLocalMapping) {
                return Objects.equals(other.toString(), this.toString());
            }
            return false;
        }

        @Override
        public String toString() {
            return String.format("%s | %s", remoteBaseUri, localBasePath);
        }
    }


    private boolean enableTranslation;
    private RemoteToLocalMapping[] remoteToLocalMap;

    public boolean isEnableTranslation() {
        return enableTranslation;
    }

    public void setEnableTranslation(boolean enableTranslation) {
        this.enableTranslation = enableTranslation;
    }

    public RemoteToLocalMapping[] getRemoteToLocalMap() {
        return remoteToLocalMap;
    }

    public void setRemoteToLocalMap(RemoteToLocalMapping[] remoteToLocalMap) {
        this.remoteToLocalMap = remoteToLocalMap;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof MountPointsConfig) {
            return Objects.equals(other.toString(), this.toString());
        }
        return false;
    }

    @Override
    public String toString() {
        String xs = Arrays.toString(remoteToLocalMap);
        return String.format("%s | %s", enableTranslation, xs);
    }

}

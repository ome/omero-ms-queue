package ome.smuggler.core.types;

import java.nio.file.Path;
import java.time.Duration;

/**
 * Provides read-only, type-safe access to the OME CLI configuration.
 * @see ome.smuggler.config.items.OmeCliConfig
 */
public interface OmeCliConfigSource {

    /**
     * @return the path to the self-contained OME CLI jar.
     */
    Path omeCliJar();

    /**
     * @return the interval of time at which to ping OMERO to keep a session
     * alive.
     */
    Duration sessionKeepAliveInterval();

}

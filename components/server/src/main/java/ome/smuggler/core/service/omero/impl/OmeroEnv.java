package ome.smuggler.core.service.omero.impl;

import static java.util.Objects.requireNonNull;

import kew.core.msg.ChannelSource;
import ome.smuggler.core.service.file.RemotePathResolver;
import ome.smuggler.core.service.log.LogService;
import ome.smuggler.core.types.OmeCliConfigSource;
import ome.smuggler.core.types.QueuedOmeroKeepAlive;

/**
 * Provides access to service configuration and auxiliary services such as
 * logging.
 */
public class OmeroEnv {

    private final OmeCliConfigSource config;
    private final ChannelSource<QueuedOmeroKeepAlive> sessionQ;
    private final RemotePathResolver fileResolver;
    private final OmeroLogger log;

    /**
     * Creates a new instance.
     * @param config provides access the the configuration values.
     * @param sessionSourceChannel provides access to the session queue.
     * @param fileResolver resolves remote paths to local ones.
     * @param log the log service.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public OmeroEnv(OmeCliConfigSource config,
                    ChannelSource<QueuedOmeroKeepAlive> sessionSourceChannel,
                    RemotePathResolver fileResolver,
                    LogService log) {
        requireNonNull(config, "config");
        requireNonNull(sessionSourceChannel, "sessionSourceChannel");
        requireNonNull(fileResolver, "fileResolver");
        requireNonNull(log, "log");

        this.config = config;
        this.sessionQ = sessionSourceChannel;
        this.fileResolver = fileResolver;
        this.log = new OmeroLogger(log);
    }

    /**
     * @return the configuration values.
     */
    public OmeCliConfigSource config() {
        return config;
    }

    /**
     * @return the session queue.
     */
    public ChannelSource<QueuedOmeroKeepAlive> sessionQ() {
        return sessionQ;
    }

    /**
     * @return the remote-to-local file resolver.
     */
    public RemotePathResolver fileResolver() {
        return fileResolver;
    }

    /**
     * @return the log service.
     */
    public OmeroLogger log() {
        return log;
    }

}

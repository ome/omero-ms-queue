package ome.smuggler.config.wiring.omero;

import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kew.core.msg.ChannelSource;
import ome.smuggler.config.items.MountPointsConfig;
import ome.smuggler.core.service.file.RemotePathResolver;
import ome.smuggler.core.service.file.impl.RemotePathMapper;
import ome.smuggler.core.service.omero.ImportService;
import ome.smuggler.core.service.omero.SessionService;
import ome.smuggler.core.service.omero.impl.Importer;
import ome.smuggler.core.service.omero.impl.OmeroEnv;
import ome.smuggler.core.service.omero.impl.SessionManager;
import ome.smuggler.core.types.*;
import ome.smuggler.providers.log.LogAdapter;
import util.config.ConfigProvider;

/**
 * Spring bean wiring configuration for the OMERO service interfaces.
 */
@Configuration
public class OmeroServiceBeans {

    public RemotePathResolver fileResolver(
            ConfigProvider<MountPointsConfig> config) {
        RemoteMountConfigSource source =
                new RemoteMountConfigReader(config.first());
        List<RemoteMount> map = Collections.emptyList();
        if (source.enableTranslation()) {
            map = source.remoteToLocalMap();
        }
        return new RemotePathMapper(map);
    }

    @Bean
    public OmeroEnv omeroEnv(
            OmeCliConfigSource config,
            ChannelSource<QueuedOmeroKeepAlive> sessionSourceChannel,
            ConfigProvider<MountPointsConfig> mountsConfig) {
        return new OmeroEnv(config, sessionSourceChannel,
                            fileResolver(mountsConfig), new LogAdapter());
    }

    @Bean
    public ImportService importService(OmeroEnv env, ImportConfigSource cfg) {
        return new Importer(env, cfg);
    }

    @Bean
    public SessionService sessionService(OmeroEnv env) {
        return new SessionManager(env);
    }

}

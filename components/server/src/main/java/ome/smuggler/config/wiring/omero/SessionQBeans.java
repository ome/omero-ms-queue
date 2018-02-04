package ome.smuggler.config.wiring.omero;

import java.io.InputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kew.core.msg.ChannelSource;
import kew.core.msg.MessageSink;
import kew.core.msg.Reschedulable;
import kew.core.qchan.QChannelFactoryAdapter;
import kew.providers.artemis.qchan.ArtemisMessage;
import kew.providers.artemis.qchan.ArtemisQChannel;
import kew.providers.artemis.ServerConnector;
import util.serialization.SerializationFactory;
import util.serialization.json.JsonSerializationFactory;

import ome.smuggler.config.items.OmeroSessionQConfig;
import ome.smuggler.core.service.omero.SessionService;
import ome.smuggler.core.service.omero.impl.OmeroEnv;
import ome.smuggler.core.service.omero.impl.SessionKeepAliveHandler;
import ome.smuggler.core.types.QueuedOmeroKeepAlive;


/**
 * Singleton beans for Artemis client resources that have to be shared and
 * reused.
 */
@Configuration
public class SessionQBeans {

    @Bean
    public QChannelFactoryAdapter<ArtemisMessage, QueuedOmeroKeepAlive>
        sessionChannelFactory(
            ServerConnector connector, OmeroSessionQConfig qConfig,
            SerializationFactory sf) {
        return new ArtemisQChannel<>(connector,
                                     qConfig,
                                     sf.serializer(),
                                     sf.deserializer(QueuedOmeroKeepAlive.class));
    }

    @Bean
    public ChannelSource<QueuedOmeroKeepAlive> sessionSourceChannel(
        QChannelFactoryAdapter<ArtemisMessage, QueuedOmeroKeepAlive> factory)
            throws Exception {
        return factory.buildSource();
    }

    @Bean
    public MessageSink<ArtemisMessage, InputStream>
        dequeueSessionTask(
            QChannelFactoryAdapter<ArtemisMessage, QueuedOmeroKeepAlive> factory,
            OmeroEnv env,
            SessionService service)
                throws Exception {
        Reschedulable<QueuedOmeroKeepAlive> consumer =
                new SessionKeepAliveHandler(env, service);
        return factory.buildReschedulableSink(consumer);
    }

}

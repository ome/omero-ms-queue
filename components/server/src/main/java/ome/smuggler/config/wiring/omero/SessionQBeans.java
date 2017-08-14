package ome.smuggler.config.wiring.omero;

import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kew.core.qchan.QChannelFactory;
import kew.core.msg.ChannelSource;
import kew.core.msg.MessageSink;
import kew.core.msg.Reschedulable;
import kew.providers.artemis.qchan.ArtemisMessage;
import kew.providers.artemis.qchan.ArtemisQChannelFactory;
import kew.providers.artemis.ServerConnector;
import util.io.SinkWriter;
import util.io.SourceReader;

import ome.smuggler.config.items.OmeroSessionQConfig;
import ome.smuggler.core.service.omero.SessionService;
import ome.smuggler.core.service.omero.impl.OmeroEnv;
import ome.smuggler.core.service.omero.impl.SessionKeepAliveHandler;
import ome.smuggler.core.types.QueuedOmeroKeepAlive;
import ome.smuggler.providers.json.JsonInputStreamReader;
import ome.smuggler.providers.json.JsonOutputStreamWriter;


/**
 * Singleton beans for Artemis client resources that have to be shared and
 * reused.
 */
@Configuration
public class SessionQBeans {

    private SinkWriter<QueuedOmeroKeepAlive, OutputStream> serializer() {
        return new JsonOutputStreamWriter<>();
    }

    private SourceReader<InputStream, QueuedOmeroKeepAlive> deserializer() {
        return new JsonInputStreamReader<>(QueuedOmeroKeepAlive.class);
    }

    @Bean
    public QChannelFactory<ArtemisMessage, QueuedOmeroKeepAlive>
        sessionChannelFactory(
            ServerConnector connector, OmeroSessionQConfig qConfig) {
        return new ArtemisQChannelFactory<>(connector, qConfig);
    }

    @Bean
    public ChannelSource<QueuedOmeroKeepAlive> sessionSourceChannel(
            QChannelFactory<ArtemisMessage, QueuedOmeroKeepAlive> factory)
            throws Exception {
        return factory.buildSource(serializer());
    }

    @Bean
    public MessageSink<ArtemisMessage, InputStream>
        dequeueSessionTask(
                QChannelFactory<ArtemisMessage, QueuedOmeroKeepAlive> factory,
                OmeroEnv env,
                SessionService service)
            throws Exception {
        Reschedulable<QueuedOmeroKeepAlive> consumer =
                new SessionKeepAliveHandler(env, service);
        return factory.buildReschedulableSink(consumer,
                                              serializer(),
                                              deserializer());
    }

}

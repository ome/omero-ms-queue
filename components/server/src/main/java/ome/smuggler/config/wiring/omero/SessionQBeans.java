package ome.smuggler.config.wiring.omero;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kew.core.msg.ChannelSource;
import kew.core.msg.Reschedulable;
import ome.smuggler.config.items.OmeroSessionQConfig;
import ome.smuggler.core.service.omero.SessionService;
import ome.smuggler.core.service.omero.impl.OmeroEnv;
import ome.smuggler.core.service.omero.impl.SessionKeepAliveHandler;
import ome.smuggler.core.types.QueuedOmeroKeepAlive;
import ome.smuggler.providers.json.JsonInputStreamReader;
import ome.smuggler.providers.json.JsonOutputStreamWriter;
import ome.smuggler.providers.q.DequeueTask;
import ome.smuggler.providers.q.QChannelFactory;
import ome.smuggler.providers.q.ServerConnector;
import util.io.SinkWriter;
import util.io.SourceReader;

/**
 * Singleton beans for HornetQ client resources that have to be shared and
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
    public QChannelFactory<QueuedOmeroKeepAlive> sessionChannelFactory(
            ServerConnector connector, OmeroSessionQConfig qConfig) {
        return new QChannelFactory<>(connector, qConfig);
    }

    @Bean
    public ChannelSource<QueuedOmeroKeepAlive> sessionSourceChannel(
            QChannelFactory<QueuedOmeroKeepAlive> factory)
            throws ActiveMQException {
        return factory.buildSource(serializer());
    }

    @Bean
    public DequeueTask<QueuedOmeroKeepAlive> dequeueSessionTask(
                QChannelFactory<QueuedOmeroKeepAlive> factory,
                OmeroEnv env,
                SessionService service)
            throws ActiveMQException {
        Reschedulable<QueuedOmeroKeepAlive> consumer =
                new SessionKeepAliveHandler(env, service);
        return factory.buildReschedulableSink(consumer,
                                              serializer(),
                                              deserializer());
    }

}

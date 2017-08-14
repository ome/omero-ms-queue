package ome.smuggler.config.wiring.mail;

import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kew.core.qchan.QChannelFactory;
import kew.core.msg.ChannelSource;
import kew.core.msg.MessageSink;
import kew.core.msg.Reschedulable;
import kew.core.msg.ReschedulableFactory;
import kew.providers.artemis.qchan.ArtemisMessage;
import kew.providers.artemis.qchan.ArtemisQChannelFactory;
import kew.providers.artemis.ServerConnector;
import util.io.SinkWriter;
import util.io.SourceReader;

import ome.smuggler.config.items.MailQConfig;
import ome.smuggler.core.service.mail.FailedMailHandler;
import ome.smuggler.core.service.mail.MailProcessor;
import ome.smuggler.core.types.MailConfigSource;
import ome.smuggler.core.types.QueuedMail;
import ome.smuggler.providers.json.JsonInputStreamReader;
import ome.smuggler.providers.json.JsonOutputStreamWriter;

/**
 * Singleton beans for Artemis client resources that have to be shared and
 * reused. 
 */
@Configuration
public class MailQBeans {

    private SinkWriter<QueuedMail, OutputStream> serializer() {
        return new JsonOutputStreamWriter<>();
    }

    private SourceReader<InputStream, QueuedMail> deserializer() {
        return new JsonInputStreamReader<>(QueuedMail.class);
    }

    @Bean
    public QChannelFactory<ArtemisMessage, QueuedMail> mailChannelFactory(
            ServerConnector connector, MailQConfig qConfig) {
        return new ArtemisQChannelFactory<>(connector, qConfig);
    }
    
    @Bean
    public ChannelSource<QueuedMail> mailSourceChannel(
            QChannelFactory<ArtemisMessage, QueuedMail> factory)
            throws Exception {
        return factory.buildSource(serializer());
    }
    
    @Bean
    public MessageSink<ArtemisMessage, InputStream> dequeueMailTask(
            QChannelFactory<ArtemisMessage, QueuedMail> factory,
            MailConfigSource mailConfig,
            MailProcessor processor,
            FailedMailHandler failureHandler) throws Exception {
        Reschedulable<QueuedMail> consumer = 
                ReschedulableFactory.buildForRepeatConsumer(processor, 
                        mailConfig.retryIntervals(), failureHandler);
        return factory.buildReschedulableSink(consumer,
                                              serializer(),
                                              deserializer());
    }

}

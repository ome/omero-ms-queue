package ome.smuggler.config.wiring.mail;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kew.core.msg.ChannelSource;
import kew.core.msg.MessageSink;
import kew.core.qchan.QChannelFactoryAdapter;
import kew.providers.artemis.ServerConnector;
import kew.providers.artemis.qchan.ArtemisMessage;
import kew.providers.artemis.qchan.ArtemisQChannel;
import util.serialization.SerializationFactory;
import util.serialization.json.JsonSerializationFactory;

import ome.smuggler.config.items.MailQConfig;
import ome.smuggler.core.service.mail.FailedMailHandler;
import ome.smuggler.core.service.mail.MailProcessor;
import ome.smuggler.core.types.MailConfigSource;
import ome.smuggler.core.types.QueuedMail;

/**
 * Singleton beans for Artemis client resources that have to be shared and
 * reused. 
 */
@Configuration
public class MailQBeans {

    @Bean
    public QChannelFactoryAdapter<ArtemisMessage, QueuedMail>
    mailChannelFactory(ServerConnector connector, MailQConfig qConfig,
                       SerializationFactory sf) {
        return new ArtemisQChannel<>(connector,
                                     qConfig,
                                     sf.serializer(),
                                     sf.deserializer(QueuedMail.class));
    }
    
    @Bean
    public ChannelSource<QueuedMail> mailSourceChannel(
            QChannelFactoryAdapter<ArtemisMessage, QueuedMail> factory)
            throws Exception {
        return factory.buildSource();
    }
    
    @Bean
    public MessageSink<ArtemisMessage, InputStream> dequeueMailTask(
            QChannelFactoryAdapter<ArtemisMessage, QueuedMail> factory,
            MailConfigSource mailConfig,
            MailProcessor processor,
            FailedMailHandler failureHandler) throws Exception {
        return factory.buildRepeatSink(processor,
                                       mailConfig.retryIntervals(),
                                       failureHandler);
    }

}

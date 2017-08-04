package ome.smuggler.config.wiring.imports;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kew.core.qchan.QChannelFactory;
import kew.core.msg.ChannelSource;
import kew.core.msg.MessageSink;
import kew.core.msg.Reschedulable;
import kew.core.msg.ReschedulableFactory;
import kew.providers.artemis.ArtemisMessage;
import kew.providers.artemis.ArtemisQChannelFactory;
import kew.providers.artemis.ServerConnector;

import ome.smuggler.config.wiring.crypto.SerializationFactory;
import ome.smuggler.config.items.ImportQConfig;
import ome.smuggler.core.service.imports.FailedImportHandler;
import ome.smuggler.core.service.imports.ImportProcessor;
import ome.smuggler.core.types.ImportConfigSource;
import ome.smuggler.core.types.QueuedImport;

/**
 * Singleton beans for Artemis client resources that have to be shared and
 * reused. 
 */
@Configuration
public class ImportQBeans {

    @Autowired
    private SerializationFactory sf;

    @Bean
    public QChannelFactory<ArtemisMessage, QueuedImport> importChannelFactory(
            ServerConnector connector, ImportQConfig qConfig) {
        return new ArtemisQChannelFactory<>(connector, qConfig);
    }
    
    @Bean
    public ChannelSource<QueuedImport> importSourceChannel(
            QChannelFactory<ArtemisMessage, QueuedImport> factory)
            throws Exception {
        return factory.buildSource(sf.serializer());
    }
    
    @Bean
    public MessageSink<ArtemisMessage, InputStream> dequeueImportTask(
            QChannelFactory<ArtemisMessage, QueuedImport> factory,
            ImportConfigSource importConfig,
            ImportProcessor processor,
            FailedImportHandler failureHandler) throws Exception {
        Reschedulable<QueuedImport> consumer = 
                ReschedulableFactory.buildForRepeatConsumer(processor, 
                        importConfig.retryIntervals(), failureHandler);
        return factory.buildReschedulableSink(
                consumer,
                sf.serializer(),
                sf.deserializer(QueuedImport.class));
    }
    
}

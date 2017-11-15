package ome.smuggler.config.wiring.imports;

import java.io.InputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kew.core.msg.ChannelSource;
import kew.core.msg.MessageSink;
import kew.core.qchan.QChannelFactoryAdapter;
import kew.providers.artemis.qchan.ArtemisMessage;
import kew.providers.artemis.qchan.ArtemisQChannel;
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

    @Bean
    public QChannelFactoryAdapter<ArtemisMessage, QueuedImport>
    importChannelFactory(ServerConnector connector,
                         ImportQConfig qConfig,
                         SerializationFactory sf) {
        return new ArtemisQChannel<>(connector,
                                     qConfig,
                                     sf.serializer(),
                                     sf.deserializer(QueuedImport.class));
    }
    
    @Bean
    public ChannelSource<QueuedImport> importSourceChannel(
            QChannelFactoryAdapter<ArtemisMessage, QueuedImport> factory)
            throws Exception {
        return factory.buildSource();
    }
    
    @Bean
    public MessageSink<ArtemisMessage, InputStream> dequeueImportTask(
            QChannelFactoryAdapter<ArtemisMessage, QueuedImport> factory,
            ImportConfigSource importConfig,
            ImportProcessor processor,
            FailedImportHandler failureHandler) throws Exception {
        return factory.buildRepeatSink(processor,
                                       importConfig.retryIntervals(),
                                       failureHandler);
    }
    
}

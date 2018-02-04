package ome.smuggler.config.wiring.imports;

import java.io.InputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kew.core.msg.MessageSink;
import kew.core.msg.SchedulingSource;
import kew.core.qchan.QChannelFactoryAdapter;
import kew.providers.artemis.qchan.ArtemisMessage;
import kew.providers.artemis.qchan.ArtemisQChannel;
import kew.providers.artemis.ServerConnector;

import ome.smuggler.config.items.ImportGcQConfig;
import ome.smuggler.core.service.imports.FailedFinalisationHandler;
import ome.smuggler.core.service.imports.ImportFinaliser;
import ome.smuggler.core.types.ImportConfigSource;
import ome.smuggler.core.types.ProcessedImport;
import util.serialization.SerializationFactory;

/**
 * Singleton beans for Artemis client resources that have to be shared and
 * reused. 
 */
@Configuration
public class ImportGcQBeans {

    @Bean
    public QChannelFactoryAdapter<ArtemisMessage, ProcessedImport>
        importGcChannelFactory(ServerConnector connector,
                               ImportGcQConfig qConfig,
                               SerializationFactory sf) {
        return new ArtemisQChannel<>(connector,
                                     qConfig,
                                     sf.serializer(),
                                     sf.deserializer(ProcessedImport.class));
    }
    
    @Bean
    public SchedulingSource<ProcessedImport> importGcSourceChannel(
        QChannelFactoryAdapter<ArtemisMessage, ProcessedImport> factory)
            throws Exception {
        return factory.buildSchedulingSource();
    }
    
    @Bean
    public MessageSink<ArtemisMessage, InputStream>
        dequeueImportFinaliserTask(
            QChannelFactoryAdapter<ArtemisMessage, ProcessedImport> factory,
            ImportConfigSource importConfig,
            ImportFinaliser finaliser,
            FailedFinalisationHandler failureHandler) throws Exception {
        return factory.buildRepeatSink(finaliser,
                                       importConfig.retryIntervals(),
                                       failureHandler);
    }

}

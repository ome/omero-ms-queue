package ome.smuggler.config.wiring.imports;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kew.core.msg.MessageSink;
import kew.core.msg.Reschedulable;
import kew.core.msg.ReschedulableFactory;
import kew.core.msg.SchedulingSource;
import kew.core.qchan.QChannelFactory;
import kew.providers.artemis.qchan.ArtemisMessage;
import kew.providers.artemis.qchan.ArtemisQChannelFactory;
import kew.providers.artemis.ServerConnector;

import ome.smuggler.config.wiring.crypto.SerializationFactory;
import ome.smuggler.config.items.ImportGcQConfig;
import ome.smuggler.core.service.imports.FailedFinalisationHandler;
import ome.smuggler.core.service.imports.ImportFinaliser;
import ome.smuggler.core.types.ImportConfigSource;
import ome.smuggler.core.types.ProcessedImport;

/**
 * Singleton beans for Artemis client resources that have to be shared and
 * reused. 
 */
@Configuration
public class ImportGcQBeans {

    @Autowired
    private SerializationFactory sf;

    @Bean
    public QChannelFactory<ArtemisMessage, ProcessedImport>
        importGcChannelFactory(
            ServerConnector connector, ImportGcQConfig qConfig) {
        return new ArtemisQChannelFactory<>(connector, qConfig);
    }
    
    @Bean
    public SchedulingSource<ProcessedImport> importGcSourceChannel(
            QChannelFactory<ArtemisMessage, ProcessedImport> factory)
            throws Exception {
        return factory.buildSchedulingSource(sf.serializer());
    }
    
    @Bean
    public MessageSink<ArtemisMessage, InputStream>
        dequeueImportFinaliserTask(
            QChannelFactory<ArtemisMessage, ProcessedImport> factory,
            ImportConfigSource importConfig,
            ImportFinaliser finaliser,
            FailedFinalisationHandler failureHandler) throws Exception {
        Reschedulable<ProcessedImport> consumer =
                ReschedulableFactory.buildForRepeatConsumer(
                        finaliser,
                        importConfig.retryIntervals(),
                        failureHandler);
        return factory.buildReschedulableSink(
                consumer,
                sf.serializer(),
                sf.deserializer(ProcessedImport.class));
    }

}

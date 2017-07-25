package ome.smuggler.config.wiring.imports;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kew.core.msg.ChannelSource;
import kew.core.msg.Reschedulable;
import kew.core.msg.ReschedulableFactory;
import ome.smuggler.config.wiring.crypto.SerializationFactory;
import ome.smuggler.config.items.ImportQConfig;
import ome.smuggler.core.service.imports.FailedImportHandler;
import ome.smuggler.core.service.imports.ImportProcessor;
import ome.smuggler.core.types.ImportConfigSource;
import ome.smuggler.core.types.QueuedImport;
import ome.smuggler.providers.q.DequeueTask;
import ome.smuggler.providers.q.QChannelFactory;
import ome.smuggler.providers.q.ServerConnector;

/**
 * Singleton beans for HornetQ client resources that have to be shared and
 * reused. 
 */
@Configuration
public class ImportQBeans {

    @Autowired
    private SerializationFactory sf;

    @Bean
    public QChannelFactory<QueuedImport> importChannelFactory(
            ServerConnector connector, ImportQConfig qConfig) {
        return new QChannelFactory<>(connector, qConfig);
    }
    
    @Bean
    public ChannelSource<QueuedImport> importSourceChannel(
            QChannelFactory<QueuedImport> factory) throws ActiveMQException {
        return factory.buildSource(sf.serializer());
    }
    
    @Bean
    public DequeueTask<QueuedImport> dequeueImportTask(
            QChannelFactory<QueuedImport> factory,
            ImportConfigSource importConfig,
            ImportProcessor processor,
            FailedImportHandler failureHandler) throws ActiveMQException {
        Reschedulable<QueuedImport> consumer = 
                ReschedulableFactory.buildForRepeatConsumer(processor, 
                        importConfig.retryIntervals(), failureHandler);
        return factory.buildReschedulableSink(
                consumer,
                sf.serializer(),
                sf.deserializer(QueuedImport.class));
    }
    
}

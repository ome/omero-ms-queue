package ome.smuggler.config.wiring.imports;

import org.hornetq.api.core.HornetQException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ome.smuggler.config.wiring.crypto.SerializationFactory;
import ome.smuggler.config.items.ImportGcQConfig;
import ome.smuggler.core.msg.Reschedulable;
import ome.smuggler.core.msg.ReschedulableFactory;
import ome.smuggler.core.msg.SchedulingSource;
import ome.smuggler.core.service.imports.FailedFinalisationHandler;
import ome.smuggler.core.service.imports.ImportFinaliser;
import ome.smuggler.core.types.ImportConfigSource;
import ome.smuggler.core.types.ProcessedImport;
import ome.smuggler.providers.q.DequeueTask;
import ome.smuggler.providers.q.QChannelFactory;
import ome.smuggler.providers.q.ServerConnector;

/**
 * Singleton beans for HornetQ client resources that have to be shared and
 * reused. 
 */
@Configuration
public class ImportGcQBeans {

    @Autowired
    private SerializationFactory sf;

    @Bean
    public QChannelFactory<ProcessedImport> importGcChannelFactory(
            ServerConnector connector, ImportGcQConfig qConfig) 
                    throws HornetQException {
        return new QChannelFactory<>(connector, qConfig);
    }
    
    @Bean
    public SchedulingSource<ProcessedImport> importGcSourceChannel(
            QChannelFactory<ProcessedImport> factory) throws HornetQException {
        return factory.buildSchedulingSource(sf.serializer());
    }
    
    @Bean
    public DequeueTask<ProcessedImport> dequeueImportFinaliserTask(
            QChannelFactory<ProcessedImport> factory,
            ImportConfigSource importConfig,
            ImportFinaliser finaliser,
            FailedFinalisationHandler failureHandler) throws HornetQException {
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

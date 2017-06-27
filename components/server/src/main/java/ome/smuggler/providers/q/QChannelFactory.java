package ome.smuggler.providers.q;

import static java.util.Objects.requireNonNull;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Function;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.core.config.CoreQueueConfiguration;

import ome.smuggler.core.convert.SinkWriter;
import ome.smuggler.core.convert.SourceReader;
import ome.smuggler.core.msg.ChannelSink;
import ome.smuggler.core.msg.ChannelSource;
import ome.smuggler.core.msg.CountedSchedule;
import ome.smuggler.core.msg.MessageSink;
import ome.smuggler.core.msg.MessageSource;
import ome.smuggler.core.msg.Reschedulable;
import ome.smuggler.core.msg.ReschedulingSink;
import ome.smuggler.core.msg.SchedulingSource;


public class QChannelFactory<T> {

    public static <T> QChannelFactory<T> with(ServerConnector connector, 
                                              CoreQueueConfiguration qConfig) {
        return new QChannelFactory<>(connector, qConfig);
    }
    
    
    private final ServerConnector connector;
    private final CoreQueueConfiguration qConfig;
    
    public QChannelFactory(ServerConnector connector, 
                           CoreQueueConfiguration qConfig) {
        requireNonNull(connector, "connector");
        requireNonNull(qConfig, "qConfig");
        
        this.connector = connector;
        this.qConfig = qConfig;
    }
   
    private QueueConnector queue() {
        return new QueueConnector(qConfig, connector.getSession());
    }
    
    public ChannelSource<T> buildSource(SinkWriter<T, OutputStream> serializer)
            throws ActiveMQException {
        MessageSource<Function<QueueConnector, ClientMessage>, T> task = // (*) 
                new EnqueueTask<>(queue(), serializer);
        return task.asDataSource();  
    }
    /* (*) If the ascended Java masters had blessed us with a slightly less
     * delectable language, a one-liner might have worked: 
     * 
     *      return new EnqueueTask<>(q).asDataSource();
     *      
     * But instead we shall rejoice in type erasure and accept the delightful 
     * verbosity of Java with unstinting devotion. I shall repent of even 
     * mentioning this!
     * For the record, note that using a concrete type works:
     * 
     *      return new EnqueueTask<QueuedImport>(q).asDataSource();
     */
    
    public SchedulingSource<T> buildSchedulingSource(
            SinkWriter<T, OutputStream> serializer) throws ActiveMQException {
        return new ScheduleTask<>(queue(), serializer);
    }
    
    public MessageSource<CountedSchedule, T> buildCountedScheduleSource(
            SinkWriter<T, OutputStream> serializer)
            throws ActiveMQException {
        return new CountedScheduleTask<>(queue(), serializer);
    }
    
    public DequeueTask<T> buildSink(ChannelSink<T> consumer,
                                    SourceReader<InputStream, T> deserializer)
            throws ActiveMQException {
        return new DequeueTask<>(queue(), consumer, deserializer, true);
    }
    
    public DequeueTask<T> buildSink(ChannelSink<T> consumer,
                                    SourceReader<InputStream, T> deserializer,
                                    boolean redeliverOnCrash)
                    throws ActiveMQException {
        return new DequeueTask<>(queue(), consumer, deserializer,
                                 redeliverOnCrash);
    }
    
    public DequeueTask<T> buildCountedScheduleSink(
            MessageSink<CountedSchedule, T> consumer,
            SourceReader<InputStream, T> deserializer)
                    throws ActiveMQException {
        CountedScheduleSink<T> sink = new CountedScheduleSink<>(consumer);
        return new DequeueTask<>(queue(), sink, deserializer, true);
    }
    
    public DequeueTask<T> buildCountedScheduleSink(
            MessageSink<CountedSchedule, T> consumer,
            SourceReader<InputStream, T> deserializer,
            boolean redeliverOnCrash) 
                    throws ActiveMQException {
        CountedScheduleSink<T> sink = new CountedScheduleSink<>(consumer);
        return new DequeueTask<>(queue(), sink, deserializer, redeliverOnCrash);
    }
    
    public DequeueTask<T> buildReschedulableSink(
            Reschedulable<T> consumer,
            SinkWriter<T, OutputStream> serializer,
            SourceReader<InputStream, T> deserializer) throws ActiveMQException {
        return buildReschedulableSink(consumer, serializer, deserializer, true);
    }
    
    public DequeueTask<T> buildReschedulableSink(
            Reschedulable<T> consumer,
            SinkWriter<T, OutputStream> serializer,
            SourceReader<InputStream, T> deserializer,
            boolean redeliverOnCrash)
                    throws ActiveMQException {
        MessageSource<CountedSchedule, T> loopback = 
                buildCountedScheduleSource(serializer);
        ReschedulingSink<T> sink = new ReschedulingSink<>(consumer, loopback);
        return buildCountedScheduleSink(sink, deserializer, redeliverOnCrash);
    }

}

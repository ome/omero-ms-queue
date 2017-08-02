package ome.smuggler.providers.q;

import static java.util.Objects.requireNonNull;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.activemq.artemis.core.config.CoreQueueConfiguration;

import kew.core.msg.ChannelSink;
import kew.core.msg.ChannelSource;
import kew.core.msg.CountedSchedule;
import kew.core.msg.MessageSink;
import kew.core.msg.MessageSource;
import kew.core.msg.Reschedulable;
import kew.core.msg.ReschedulingSink;
import kew.core.msg.SchedulingSource;
import kew.core.qchan.*;
import kew.core.qchan.spi.QConnector;
import kew.core.qchan.spi.QMsgBuilder;
import util.io.SinkWriter;
import util.io.SourceReader;

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

    private QConnector<ArtemisMessage> queue() {
        return new ArtemisQConnector(qConfig, connector.getSession());
    }

    public ChannelSource<T> buildSource(SinkWriter<T, OutputStream> serializer)
            throws Exception {
        MessageSource<QMsgBuilder<ArtemisMessage>, T> task = // (*)
                new EnqueueTask<>(queue().newProducer(), serializer);
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
            SinkWriter<T, OutputStream> serializer) throws Exception {
        return new ScheduleTask<>(queue().newProducer(), serializer);
    }

    public MessageSource<CountedSchedule, T> buildCountedScheduleSource(
            SinkWriter<T, OutputStream> serializer)
            throws Exception {
        return new CountedScheduleTask<>(queue().newProducer(), serializer);
    }

    public DequeueTask<ArtemisMessage, T> buildSink(
            ChannelSink<T> consumer, SourceReader<InputStream, T> deserializer)
            throws Exception {
        return new DequeueTask<>(queue(), consumer, deserializer, true);
    }

    public DequeueTask<ArtemisMessage, T> buildSink(
            ChannelSink<T> consumer,
            SourceReader<InputStream, T> deserializer,
            boolean redeliverOnCrash)
                    throws Exception {
        return new DequeueTask<>(queue(), consumer, deserializer,
                                 redeliverOnCrash);
    }

    public DequeueTask<ArtemisMessage, T> buildCountedScheduleSink(
            MessageSink<CountedSchedule, T> consumer,
            SourceReader<InputStream, T> deserializer)
                    throws Exception {
        CountedScheduleSink<ArtemisMessage, T> sink =
                new CountedScheduleSink<>(consumer);
        return new DequeueTask<>(queue(), sink, deserializer, true);
    }

    public DequeueTask<ArtemisMessage, T> buildCountedScheduleSink(
            MessageSink<CountedSchedule, T> consumer,
            SourceReader<InputStream, T> deserializer,
            boolean redeliverOnCrash) 
                    throws Exception {
        CountedScheduleSink<ArtemisMessage, T> sink =
                new CountedScheduleSink<>(consumer);
        return new DequeueTask<>(queue(), sink, deserializer, redeliverOnCrash);
    }

    public DequeueTask<ArtemisMessage, T> buildReschedulableSink(
            Reschedulable<T> consumer,
            SinkWriter<T, OutputStream> serializer,
            SourceReader<InputStream, T> deserializer) throws Exception {
        return buildReschedulableSink(consumer, serializer, deserializer, true);
    }

    public DequeueTask<ArtemisMessage, T> buildReschedulableSink(
            Reschedulable<T> consumer,
            SinkWriter<T, OutputStream> serializer,
            SourceReader<InputStream, T> deserializer,
            boolean redeliverOnCrash)
                    throws Exception {
        MessageSource<CountedSchedule, T> loopback = 
                buildCountedScheduleSource(serializer);
        ReschedulingSink<T> sink = new ReschedulingSink<>(consumer, loopback);
        return buildCountedScheduleSink(sink, deserializer, redeliverOnCrash);
    }

}

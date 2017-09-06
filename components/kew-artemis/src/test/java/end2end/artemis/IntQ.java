package end2end.artemis;


import kew.core.msg.ChannelSink;
import kew.core.msg.ChannelSource;
import kew.core.msg.MessageSink;
import kew.core.qchan.QChannelFactory;
import kew.providers.artemis.ServerConnector;
import kew.providers.artemis.qchan.ArtemisMessage;
import kew.providers.artemis.qchan.ArtemisQChannelFactory;
import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.config.CoreQueueConfiguration;
import util.io.SinkWriter;
import util.io.SourceReader;

import java.io.InputStream;
import java.io.OutputStream;

public class IntQ {

    private static SinkWriter<Integer, OutputStream> serializer() {
        return OutputStream::write;
    }

    private static SourceReader<InputStream, Integer> deserializer() {
        return InputStream::read;
    }

    public static CoreQueueConfiguration qConfig() {
        String address = "jms/test/intq";
        return new CoreQueueConfiguration()
              .setName(address)
              .setAddress(address)
              .setDurable(false);
    }

    public static void deploy(Configuration cfg) {
        cfg.addQueueConfiguration(qConfig());
    }


    private final QChannelFactory<ArtemisMessage, Integer> factory;

    public IntQ(ServerConnector connector) {
        this.factory = new ArtemisQChannelFactory<>(connector, qConfig());
    }

    public ChannelSource<Integer> sourceChannel() throws Exception {
        return factory.buildSource(serializer());
    }

    public MessageSink<ArtemisMessage, InputStream> sinkChannel(
            ChannelSink<Integer> sink)
                throws Exception{
        return factory.buildSink(sink, deserializer());
    }

}

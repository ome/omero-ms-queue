package end2end.artemis;

import kew.providers.artemis.ServerConnector;
import kew.providers.artemis.qchan.ArtemisQChannel;
import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.config.CoreQueueConfiguration;
import util.io.SinkWriter;
import util.io.SourceReader;

import java.io.InputStream;
import java.io.OutputStream;

public class IntQ extends ArtemisQChannel<Integer> {

    private static SinkWriter<Integer, OutputStream> serializer() {
        return OutputStream::write;
    }

    private static SourceReader<InputStream, Integer> deserializer() {
        return InputStream::read;
    }

    public static CoreQueueConfiguration qConfig() {
        String address = "test/intq";
        return new CoreQueueConfiguration()
              .setName(address)
              .setAddress(address)
              .setDurable(false);
    }

    public static void deploy(Configuration cfg) {
        cfg.addQueueConfiguration(qConfig());
    }

    public IntQ(ServerConnector connector) {
        super(connector, qConfig(), serializer(), deserializer());
    }

}

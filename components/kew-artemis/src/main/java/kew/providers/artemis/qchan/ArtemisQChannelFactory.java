package kew.providers.artemis.qchan;

import static java.util.Objects.requireNonNull;

import kew.providers.artemis.ServerConnector;
import org.apache.activemq.artemis.core.config.CoreQueueConfiguration;

import kew.core.qchan.QChannelFactory;
import kew.core.qchan.spi.QConnector;


/**
 * Factory methods to create messaging channels backed by Artemis queues.
 * @param <T> the type of the message data.
 */
public class ArtemisQChannelFactory<T>
        implements QChannelFactory<ArtemisMessage, T> {

    private final QConnector<ArtemisMessage> qConnector;

    /**
     * Creates a new instance.
     * @param connector handle to connect to the Artemis server.
     * @param qConfig the configuration of the queue to use.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public ArtemisQChannelFactory(ServerConnector connector,
                                  CoreQueueConfiguration qConfig) {
        requireNonNull(connector, "connector");
        requireNonNull(qConfig, "qConfig");

        qConnector = new ArtemisQConnector(qConfig, connector.getSession());
    }

    @Override
    public QConnector<ArtemisMessage> queue() {
        return qConnector;
    }

}

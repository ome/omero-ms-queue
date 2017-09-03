package end2end.artemis;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.error.Exceptions.unchecked;

import kew.core.msg.ChannelSource;
import kew.providers.artemis.ServerConnector;
import kew.providers.artemis.config.transport.ConnectorConfig;
import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.api.core.client.ActiveMQClient;
import org.apache.activemq.artemis.api.core.client.ServerLocator;
import org.junit.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClusterLoadBalancingTest extends BaseEmbeddedClusterTest {

    static ServerConnector startClientSession(ConnectorConfig server)
            throws Exception {
        TransportConfiguration connector = server.transport();
        ServerLocator locator =
                ActiveMQClient.createServerLocatorWithHA(connector);

        return new ServerConnector(locator);
    }

    @Test
    public void verifyRoundRobinMessageDelivery() throws Exception {
        //ServerConnector session1 = server1.startClientSession();
        //ServerConnector session2 = server2.startClientSession();
        ServerConnector session1 = startClientSession(endpoints1.connector());
        ServerConnector session2 = startClientSession(endpoints2.connector());


        IntQ q1 = new IntQ(session1);
        ChannelSource<Integer> producer = q1.sourceChannel();

        QReceiveBuffer<Integer> consumer1 = new QReceiveBuffer<>();
        q1.sinkChannel(consumer1);

        IntQ q2 = new IntQ(session2);
        QReceiveBuffer<Integer> consumer2 = new QReceiveBuffer<>();
        q2.sinkChannel(consumer2);

        Set<Integer> data = Stream.of(1, 2, 3, 4)
                                  .collect(Collectors.toSet());
        data.forEach(unchecked(producer::send));

        Set<Integer> received1 = consumer1.waitForMessages(2, 30000);
        Set<Integer> received2 = consumer2.waitForMessages(2, 30000);

        session1.close();
        session2.close();

        assertThat(received1, hasSize(2));
        assertThat(received2, hasSize(2));
    }

}

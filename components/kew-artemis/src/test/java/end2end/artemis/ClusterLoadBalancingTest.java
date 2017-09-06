package end2end.artemis;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.error.Exceptions.unchecked;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import kew.core.msg.ChannelSource;
import kew.providers.artemis.ServerConnector;

import org.junit.Test;

public class ClusterLoadBalancingTest extends BaseEmbeddedClusterTest {

    @Test
    public void verifyRoundRobinMessageDelivery() throws Exception {
        ServerConnector embeddedSession = startClientSessionWithEmbeddedServer();
        ServerConnector externalSession = startClientSessionWithExternalServer();


        IntQ q1 = new IntQ(embeddedSession);
        ChannelSource<Integer> producer = q1.sourceChannel();

        QReceiveBuffer<Integer> consumer1 = new QReceiveBuffer<>();
        q1.sinkChannel(consumer1);

        IntQ q2 = new IntQ(externalSession);
        QReceiveBuffer<Integer> consumer2 = new QReceiveBuffer<>();
        q2.sinkChannel(consumer2);

        Set<Integer> data = Stream.of(1, 2, 3, 4)
                                  .collect(Collectors.toSet());
        data.forEach(unchecked(producer::send));

        Set<Integer> received1 = consumer1.waitForMessages(2, 30000);
        Set<Integer> received2 = consumer2.waitForMessages(2, 30000);

        embeddedSession.close();
        externalSession.close();

        assertThat(received1, hasSize(2));
        assertThat(received2, hasSize(2));
    }

}

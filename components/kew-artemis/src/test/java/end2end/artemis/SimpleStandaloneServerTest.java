package end2end.artemis;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static kew.providers.artemis.config.security.SecurityProps.securityEnabled;
import static util.error.Exceptions.unchecked;

import kew.core.msg.ChannelSource;
import kew.providers.artemis.ServerConnector;
import kew.providers.artemis.config.CoreConfigFactory;
import kew.providers.artemis.runtime.EmbeddedServer;
import org.junit.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimpleStandaloneServerTest
        extends BaseStandaloneEmbeddedServerTest {

    @Test
    public void startWithBareConfig() throws Exception {
        start(CoreConfigFactory.empty());
        assertNotNull(server.instance());
        assertTrue(server.instance().isActive());
    }

    @Test
    public void sendReceiveMessage() throws Exception {
        start(CoreConfigFactory.empty()
                               .with(securityEnabled(false))
                               .with(IntQ::deploy));
        ServerConnector session = startClientSession();

        IntQ q = new IntQ(session);
        ChannelSource<Integer> producer = q.buildSource();

        QReceiveBuffer<Integer> consumer = new QReceiveBuffer<>();
        q.buildSink(consumer);

        Set<Integer> data = Stream.of(1, 2, 3).collect(Collectors.toSet());
        data.forEach(unchecked(producer::send));
        Set<Integer> received = consumer.waitForMessages(3, 30000);

        session.close();

        assertThat(received, is(data));
    }

    @Test (expected = NullPointerException.class)
    public void startThrowsIfNullSpec() throws Exception {
        EmbeddedServer.start(null);
    }

}

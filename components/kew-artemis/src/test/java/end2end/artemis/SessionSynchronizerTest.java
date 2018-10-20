package end2end.artemis;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static kew.providers.artemis.config.security.SecurityProps.securityEnabled;

import kew.core.msg.ChannelSink;
import kew.core.msg.ChannelSource;
import kew.providers.artemis.ServerConnector;
import kew.providers.artemis.config.CoreConfigFactory;
import org.junit.Rule;
import org.junit.Test;

import java.util.stream.IntStream;


public class SessionSynchronizerTest
        extends BaseStandaloneEmbeddedServerTest {

    private static final String concurrentSessionAccessWarning =
            "WARN: AMQ212051: Invalid concurrent session usage.";
    // see: ActiveMQClientLogger.LOGGER.invalidConcurrentSessionUsage

    @Rule
    public SystemOutRule capturedOutput = new SystemOutRule();

    @Test
    public void simulateSharedSessionUsage() throws Exception {
        start(CoreConfigFactory.empty()
                .with(securityEnabled(false))
                .with(IntQ::deploy));
        ServerConnector session = startClientSession();

        IntQ q = new IntQ(session);

        ChannelSource<Integer> producer = q.buildSource();

        ChannelSink<Integer> consumer = x -> {
            if (x == 1) {
                producer.uncheckedSend(2);
            }
        };
        q.buildSink(consumer);

        IntStream.iterate(1, i -> 1)
                 .limit(100)
                 .forEach(producer::uncheckedSend);

        Thread.sleep(1000);
        session.close();

        assertThat(capturedOutput.asString(),
                   not(containsString(concurrentSessionAccessWarning)));
    }

}

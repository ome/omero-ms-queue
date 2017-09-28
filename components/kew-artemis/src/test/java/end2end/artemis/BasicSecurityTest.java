package end2end.artemis;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static kew.providers.artemis.config.security.SecurityProps.securityEnabled;

import java.util.Set;

import org.apache.activemq.artemis.api.core.ActiveMQSecurityException;

import kew.core.msg.ChannelSource;
import kew.providers.artemis.ServerConnector;
import kew.providers.artemis.config.CoreConfigFactory;

import org.junit.Before;
import org.junit.Test;

public class BasicSecurityTest extends BaseStandaloneEmbeddedServerTest {

    private SecurityConfigFactory security;

    private ServerConnector startReadOnlyClientSession() throws Exception {
        return startClientSession(security.readOnlyUsername(),
                security.readOnlyUsernamePassword());
    }

    private ServerConnector startReadWriteClientSession() throws Exception {
        return startClientSession(security.readWriteUsername(),
                security.readWriteUsernamePassword());
    }

    @Override
    @Before
    public void setup() throws Exception {
        super.setup();
        security = new SecurityConfigFactory();
    }

    @Test(expected = ActiveMQSecurityException.class)
    public void requireValidCredentialsToEstablishSession() throws Exception {
        start(CoreConfigFactory.empty()
                               .with(securityEnabled(true)));

        try (ServerConnector session = server.startClientSession()) {
            fail("shouldn't have allowed the connection!");
            session.close();  // (*)
        }
    }
    // (*) gets rid of warning about session var never being used.

    @Test
    public void loginWithReadOnlyUser() throws Exception {
        SecurityConfigFactory security = new SecurityConfigFactory();
        start(security.config(), security.manager());

        try (ServerConnector session = startReadOnlyClientSession()) {
            assertNotNull(session);
        }
    }

    @Test
    public void loginWithReadWriteUser() throws Exception {
        SecurityConfigFactory security = new SecurityConfigFactory();
        start(security.config(), security.manager());

        try (ServerConnector session = startReadWriteClientSession()) {
            assertNotNull(session);
        }
    }

    @Test(expected = ActiveMQSecurityException.class)
    public void readOnlyUserNotAllowedToSendMessages() throws Exception {
        SecurityConfigFactory security = new SecurityConfigFactory();
        start(security.config().with(IntQ::deploy),
              security.manager());

        try (ServerConnector session = startReadOnlyClientSession()) {
            IntQ q = new IntQ(session);
            ChannelSource<Integer> producer = q.sourceChannel();
            producer.send(123);
        }
    }

    @Test
    public void readOnlyUserCanReceiveMessages() throws Exception {
        SecurityConfigFactory security = new SecurityConfigFactory();
        start(security.config().with(IntQ::deploy),
                security.manager());

        try (ServerConnector roSession = startReadOnlyClientSession();
             ServerConnector rwSession = startReadWriteClientSession()) {
            int message = 123;

            IntQ rwQ = new IntQ(rwSession);
            ChannelSource<Integer> producer = rwQ.sourceChannel();
            producer.send(message);

            IntQ roQ = new IntQ(roSession);
            QReceiveBuffer<Integer> consumer = new QReceiveBuffer<>();
            roQ.sinkChannel(consumer);
            Set<Integer> received = consumer.waitForMessages(1, 30000);

            assertThat(received, hasItem(message));
        }
    }

    @Test
    public void readWriteUserCanSendAndReceiveMessages() throws Exception {
        SecurityConfigFactory security = new SecurityConfigFactory();
        start(security.config().with(IntQ::deploy),
              security.manager());

        try (ServerConnector session = startReadWriteClientSession()) {
            int message = 123;

            IntQ q = new IntQ(session);
            ChannelSource<Integer> producer = q.sourceChannel();
            producer.send(message);

            QReceiveBuffer<Integer> consumer = new QReceiveBuffer<>();
            q.sinkChannel(consumer);
            Set<Integer> received = consumer.waitForMessages(1, 30000);

            assertThat(received, hasItem(message));
        }
    }

}

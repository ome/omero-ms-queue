package kew.providers.artemis.config.transport;

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class TransportConfigFactoryTest {

    @Test
    public void embeddedAcceptorTransportNeverNull() {
        assertNotNull(TransportConfigFactory.makeEmbeddedAcceptorTransport());
    }

    @Test
    public void embeddedConnectorTransportNeverNull() {
        assertNotNull(TransportConfigFactory.makeEmbeddedConnectorTransport());
    }

    @Test
    public void networkAcceptorTransportNeverNull() {
        assertNotNull(TransportConfigFactory.makeNetworkAcceptorTransport());
    }

    @Test
    public void networkConnectorTransportNeverNull() {
        assertNotNull(TransportConfigFactory.makeNetworkConnectorTransport());
    }

    @Test
    public void ctor() {
        new TransportConfigFactory();  // only to get 100% coverage.
    }

}

package kew.providers.artemis.config.transport;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.apache.activemq.artemis.core.remoting.impl.invm.TransportConstants;
import org.junit.Test;


public class EmbeddedConnectorConfigTest
        extends BaseEndpointConfigTest<EmbeddedConnectorConfig> {

    @Override
    protected EmbeddedConnectorConfig target() {
        return new EmbeddedConnectorConfig();
    }

    @Test
    public void paramsContainsArtemisDefaults() {
        assertThat(target().params().size(), greaterThan(0));
    }

    @Test
    public void serverIdDefaultsToTransportConstant() {
        assertThat(target().params().get(TransportConstants.SERVER_ID_PROP_NAME),
                   is(TransportConstants.DEFAULT_SERVER_ID));
    }

}

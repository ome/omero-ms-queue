package kew.providers.artemis.config.transport;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.apache.activemq.artemis.core.remoting.impl.netty.TransportConstants;
import org.junit.Test;


public class NetworkConnectorConfigTest
        extends BaseEndpointConfigTest<NetworkConnectorConfig> {

    @Override
    protected NetworkConnectorConfig target() {
        return new NetworkConnectorConfig();
    }

    @Test
    public void paramsContainsArtemisDefaults() {
        assertThat(target().params().size(), greaterThan(0));
    }

    @Test
    public void hostDefaultsToTransportConstant() {
        assertThat(target().params().get(TransportConstants.HOST_PROP_NAME),
                   is(TransportConstants.DEFAULT_HOST));
    }

    @Test
    public void portDefaultsToTransportConstant() {
        assertThat(target().params().get(TransportConstants.PORT_PROP_NAME),
                   is(TransportConstants.DEFAULT_PORT));
    }

}

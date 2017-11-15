package kew.providers.artemis.config.transport;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;


public class NetworkAcceptorConfigTest
        extends BaseEndpointConfigTest<NetworkAcceptorConfig> {

    @Override
    protected NetworkAcceptorConfig target() {
        return new NetworkAcceptorConfig();
    }

    @Test
    public void paramsContainNoDefaults() {
        assertThat(target().params().size(), is(0));
    }

}


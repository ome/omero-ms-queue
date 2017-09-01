package kew.providers.artemis.config.transport;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;


public class EmbeddedAcceptorConfigTest
        extends BaseEndpointConfigTest<EmbeddedAcceptorConfig> {

    @Override
    protected EmbeddedAcceptorConfig target() {
        return new EmbeddedAcceptorConfig();
    }

    @Test
    public void paramsContainNoDefaults() {
        assertThat(target().params().size(), is(0));
    }

}

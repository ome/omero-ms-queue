package kew.providers.artemis.config;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.apache.activemq.artemis.core.remoting.impl.invm.TransportConstants;
import org.junit.Test;

public class EmbeddedTransportConfigTest {

    private static EmbeddedTransportConfig target() {
        return new EmbeddedTransportConfig();
    }

    @Test
    public void generateUniqueTransportName() {
        String name1 = target().get().getName();
        String name2 = target().get().getName();

        assertThat(name1, not(isEmptyOrNullString()));
        assertThat(name2, not(isEmptyOrNullString()));
        assertThat(name1, not(name2));
    }

    @Test
    public void paramsNeverNull() {
        assertNotNull(target().params());
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

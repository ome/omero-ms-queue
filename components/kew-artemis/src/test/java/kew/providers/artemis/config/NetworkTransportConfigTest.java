package kew.providers.artemis.config;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.apache.activemq.artemis.core.remoting.impl.netty.TransportConstants;
import org.junit.Test;

public class NetworkTransportConfigTest {

    private static NetworkTransportConfig target() {
        return new NetworkTransportConfig();
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
    public void hostDefaultsToLocalHost() {
        assertThat(target().params().get(TransportConstants.HOST_PROP_NAME),
                   is("localhost"));
    }

    @Test
    public void portDefaultsTo61616() {
        assertThat(target().params().get(TransportConstants.PORT_PROP_NAME),
                   is(61616));
    }

}

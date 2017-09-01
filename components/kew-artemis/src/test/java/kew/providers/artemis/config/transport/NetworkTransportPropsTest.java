package kew.providers.artemis.config.transport;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static kew.providers.artemis.config.transport.NetworkTransportProps.*;

import org.apache.activemq.artemis.core.remoting.impl.netty.TransportConstants;
import org.junit.Test;
import util.object.Builder;
import util.types.PositiveN;

import java.util.function.Function;

public class NetworkTransportPropsTest {

    private static void assertProp(
            Function<NetworkAcceptorConfig, NetworkAcceptorConfig> setter,
            String transportPropName,
            Object expected) {
        Object actual = Builder.make(NetworkAcceptorConfig::new)
                               .with(setter)
                               .apply(null)
                               .params()
                               .get(transportPropName);

        assertThat(actual, is(expected));
    }

    @Test
    public void setHost() {
        String name = "x";
        assertProp(host(name), TransportConstants.HOST_PROP_NAME, name);
    }

    @Test
    public void setPort() {
        Integer number = 123;
        assertProp(port(PositiveN.of(number)),
                   TransportConstants.PORT_PROP_NAME,
                   number);
    }

    @Test(expected = NullPointerException.class)
    public void portThrowsIfNullNumber() {
        port(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void hostThrowsIfNullName() {
        host(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void hostThrowsIfEmptyName() {
        host("");
    }

    @Test
    public void ctor() {
        new NetworkTransportProps();  // only to get 100% coverage.
    }

}

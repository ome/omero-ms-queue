package kew.providers.artemis.config.transport;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static kew.providers.artemis.config.transport.EmbeddedTransportProps.*;

import org.apache.activemq.artemis.core.remoting.impl.invm.TransportConstants;
import org.junit.Test;
import util.object.Builder;
import util.types.PositiveN;

import java.util.function.Function;

public class EmbeddedTransportPropsTest {

    private static void assertProp(
            Function<EmbeddedConnectorConfig, EmbeddedConnectorConfig> setter,
            String transportPropName,
            Object expected) {
        EmbeddedConnectorConfig connector = new EmbeddedConnectorConfig();
        Object actual = Builder.make(() -> connector)
                               .with(setter)
                               .apply(null)
                               .params()
                               .get(transportPropName);

        assertThat(actual, is(expected));
    }

    @Test
    public void setServerId() {
        Integer id = 123;
        assertProp(serverId(PositiveN.of(id)),
                   TransportConstants.SERVER_ID_PROP_NAME,
                   id);
    }

    @Test(expected = NullPointerException.class)
    public void serverIdThrowsIfNullId() {
        serverId(null);
    }

    @Test
    public void ctor() {
        new EmbeddedTransportProps();  // only to get 100% coverage.
    }

}

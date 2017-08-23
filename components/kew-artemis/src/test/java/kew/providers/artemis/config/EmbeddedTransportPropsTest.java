package kew.providers.artemis.config;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static kew.providers.artemis.config.EmbeddedTransportProps.*;

import org.apache.activemq.artemis.core.remoting.impl.invm.TransportConstants;
import org.junit.Test;
import util.object.Builder;
import util.types.PositiveN;

import java.util.function.Consumer;

public class EmbeddedTransportPropsTest {

    private static void assertProp(Consumer<EmbeddedTransportConfig> setter,
                                   String transportPropName,
                                   Object expected) {
        Object actual = Builder.make(EmbeddedTransportConfig::new)
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

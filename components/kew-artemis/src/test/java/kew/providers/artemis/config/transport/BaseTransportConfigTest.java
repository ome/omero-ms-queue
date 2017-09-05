package kew.providers.artemis.config.transport;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import kew.providers.artemis.config.CoreConfigFactory;
import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.remoting.impl.invm.InVMConnectorFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;


public abstract class BaseTransportConfigTest implements EndpointConfig {

    private TransportConfiguration transport;

    @Before
    public void setup() {
        transport = new TransportConfiguration(
                InVMConnectorFactory.class.getCanonicalName());
    }

    @Override
    public TransportConfiguration transport() {
        return transport;
    }

    protected abstract Collection<TransportConfiguration>
            getConfiguredTransports(Configuration cfg);
    protected abstract void addTransportToOverride(
            Configuration cfg, TransportConfiguration tc);

    private void assertHasOnlyThisTransport(Configuration cfg) {
        Collection<TransportConfiguration> ts = getConfiguredTransports(cfg);
        assertThat(ts, hasSize(1));
        assertThat(ts.toArray()[0], is(transport));
    }

    @Test
    public void addTransportToConfig() {
        Configuration cfg = CoreConfigFactory.empty()
                                             .with(this::transportConfig)
                                             .apply(null);
        assertHasOnlyThisTransport(cfg);
    }

    @Test
    public void overrideTransportInConfig() {
        TransportConfiguration overridden = new TransportConfiguration(
                transport.getFactoryClassName(),
                new HashMap<>(),
                transport.getName());
        overridden.getParams().put("xxx", 123);

        assertThat(transport.getName(), is(overridden.getName()));
        assertThat(transport, is(not(overridden)));

        Configuration cfg = CoreConfigFactory
                           .empty()
                           .with(c -> { addTransportToOverride(c, overridden); })
                           .with(this::transportConfig)
                           .apply(null);

        assertHasOnlyThisTransport(cfg);
    }

    @Test(expected = NullPointerException.class)
    public void transportConfigThrowsIfNullConfig() {
        transportConfig(null);
    }

}

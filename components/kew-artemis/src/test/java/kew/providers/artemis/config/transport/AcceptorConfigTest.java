package kew.providers.artemis.config.transport;

import static org.mockito.Mockito.*;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.config.Configuration;
import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public class AcceptorConfigTest
        extends BaseTransportConfigTest
        implements AcceptorConfig {

    @Override
    protected Collection<TransportConfiguration> getConfiguredTransports(
            Configuration cfg) {
        return cfg.getAcceptorConfigurations();
    }

    @Override
    protected void addTransportToOverride(Configuration cfg,
                                          TransportConfiguration tc) {
        cfg.addAcceptorConfiguration(tc);
    }

    @Test
    public void tolerateNullAcceptorConfigurations() {
        Configuration config = mock(Configuration.class);
        when(config.getAcceptorConfigurations()).thenReturn(null);
        transportConfig(config);


        verify(config).getAcceptorConfigurations();
        verify(config).addAcceptorConfiguration(any());
    }

    @Test
    public void tolerateNullTransportInAcceptorConfigurations() {
        Configuration config = mock(Configuration.class);
        Set<TransportConfiguration> acceptors = new HashSet<>();
        acceptors.add(null);
        when(config.getAcceptorConfigurations()).thenReturn(acceptors);
        transportConfig(config);

        verify(config, times(2)).getAcceptorConfigurations();
        verify(config).addAcceptorConfiguration(any());
    }

}

package kew.providers.artemis.config.transport;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.config.Configuration;

import java.util.Collection;

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

}

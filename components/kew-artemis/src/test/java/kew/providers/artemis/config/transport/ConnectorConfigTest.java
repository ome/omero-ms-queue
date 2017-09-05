package kew.providers.artemis.config.transport;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.config.Configuration;

import java.util.Collection;

public class ConnectorConfigTest
        extends BaseTransportConfigTest
        implements ConnectorConfig {

    @Override
    protected Collection<TransportConfiguration> getConfiguredTransports(
            Configuration cfg) {
        return cfg.getConnectorConfigurations().values();
    }

    @Override
    protected void addTransportToOverride(Configuration cfg,
                                          TransportConfiguration tc) {
        cfg.addConnectorConfiguration(tc.getName(), tc);
    }

}

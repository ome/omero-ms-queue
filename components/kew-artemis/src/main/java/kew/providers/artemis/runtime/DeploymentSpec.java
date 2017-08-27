package kew.providers.artemis.runtime;

import static java.util.Objects.requireNonNull;

import java.lang.management.ManagementFactory;
import java.util.Optional;
import javax.management.MBeanServer;

import kew.providers.artemis.config.EmbeddedServerTransportConfig;
import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.spi.core.security.ActiveMQJAASSecurityManager;
import org.apache.activemq.artemis.spi.core.security.ActiveMQSecurityManager;

import util.object.Builder;

/**
 * Holds data and objects required to start an Artemis server instance.
 */
public class DeploymentSpec {

    private final Configuration config;
    private final EmbeddedServerTransportConfig embeddedEndpoints;
    private final ActiveMQSecurityManager securityManager;
    private final MBeanServer mBeanServer;

    /**
     * Creates a new instance.
     * This constructor automatically adds {@link EmbeddedServerTransportConfig
     * embedded transport endpoints} to the Artemis core configuration and
     * uses Artemis's default security manager and MBean server if none are
     * provided.
     * @param configBuilder builds the core configuration.
     * @param securityManager the security manager to install or empty to use
     *                        Artemis's default.
     * @param mBeanServer the MBean server to install or empty to use Artemis's
     *                    default.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public DeploymentSpec(Builder<Void, Configuration> configBuilder,
                          Optional<ActiveMQSecurityManager> securityManager,
                          Optional<MBeanServer> mBeanServer) {
        requireNonNull(configBuilder, "configBuilder");
        requireNonNull(securityManager, "securityManager");
        requireNonNull(mBeanServer, "mBeanServer");

        this.embeddedEndpoints = new EmbeddedServerTransportConfig();
        this.config = configBuilder
                     .with(embeddedEndpoints::embeddedTransport)
                     .apply(null);
        this.securityManager = securityManager    // (1)
                              .orElseGet(ActiveMQJAASSecurityManager::new);
        this.mBeanServer = mBeanServer            // (2)
                          .orElseGet(ManagementFactory::getPlatformMBeanServer);
    }
    /* (1) same default as in EmbeddedActiveMQ
     * (2) same default as in ActiveMQServerImpl
     */

    /**
     * @return the Artemis core configuration.
     */
    public Configuration config() {
        return config;
    }

    /**
     * @return the embedded acceptor and connector that were automatically
     * added to the core configuration.
     */
    public EmbeddedServerTransportConfig embeddedEndpoints() {
        return embeddedEndpoints;
    }

    /**
     * @return the security manager to install in the Artemis server.
     */
    public ActiveMQSecurityManager securityManager() {
        return securityManager;
    }

    /**
     * @return the MBean server to install in the Artemis server.
     */
    public MBeanServer mBeanServer() {
        return mBeanServer;
    }

}

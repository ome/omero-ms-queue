package kew.providers.artemis.runtime;

import static java.util.Objects.requireNonNull;

import java.lang.management.ManagementFactory;
import java.util.Optional;
import javax.management.MBeanServer;

import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.spi.core.security.ActiveMQSecurityManager;

import kew.providers.artemis.config.transport.ServerEmbeddedEndpoints;
import util.object.Builder;

/**
 * Holds data and objects required to start an Artemis server instance.
 */
public class DeploymentSpec {

    private final Configuration config;
    private final ServerEmbeddedEndpoints embeddedEndpoints;
    private final ActiveMQSecurityManager securityManager;
    private final MBeanServer mBeanServer;

    /**
     * Creates a new instance.
     * This constructor automatically adds {@link ServerEmbeddedEndpoints
     * embedded transport endpoints} to the Artemis core configuration and
     * uses Artemis's default security manager and MBean server if none are
     * provided.
     * @param configBuilder builds the core configuration.
     * @param securityManagerBuilder builds the security manager to install.
     * @param mBeanServer the MBean server to install or empty to use Artemis's
     *                    default.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public <T extends ActiveMQSecurityManager> DeploymentSpec(
            Builder<Void, Configuration> configBuilder,
            Builder<Void, T> securityManagerBuilder,
            Optional<MBeanServer> mBeanServer) {
        requireNonNull(configBuilder, "configBuilder");
        requireNonNull(securityManagerBuilder, "securityManagerBuilder");
        requireNonNull(mBeanServer, "mBeanServer");

        this.embeddedEndpoints = new ServerEmbeddedEndpoints();
        this.config = configBuilder
                     .with(embeddedEndpoints::transportConfig)
                     .apply(null);
        this.securityManager = securityManagerBuilder.apply(null);
        this.mBeanServer = mBeanServer            // (*)
                          .orElseGet(ManagementFactory::getPlatformMBeanServer);
    }
    // (*) same default as in ActiveMQServerImpl

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
    public ServerEmbeddedEndpoints embeddedEndpoints() {
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

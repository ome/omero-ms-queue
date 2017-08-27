package kew.providers.artemis.runtime;

import static java.util.Objects.requireNonNull;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.api.core.client.ActiveMQClient;
import org.apache.activemq.artemis.api.core.client.ServerLocator;
import org.apache.activemq.artemis.core.server.ActiveMQServer;
import org.apache.activemq.artemis.core.server.impl.ActiveMQServerImpl;

import kew.providers.artemis.ServerConnector;

public class EmbeddedServer {

    public static EmbeddedServer start(DeploymentSpec spec)
            throws Exception {
        requireNonNull(spec, "spec");

        ActiveMQServer instance = new ActiveMQServerImpl(
                spec.config(), spec.mBeanServer(), spec.securityManager());
        instance.start();

        return new EmbeddedServer(spec, instance);
    }

    private final DeploymentSpec deploymentSpec;
    private final ActiveMQServer instance;

    private EmbeddedServer(DeploymentSpec spec, ActiveMQServer instance) {
        this.deploymentSpec = spec;
        this.instance = instance;
    }

    public ServerConnector startClientSession() throws Exception {
        TransportConfiguration connector = deploymentSpec.embeddedEndpoints()
                                                         .embeddedConnector();
        ServerLocator locator = null;
        if (deploymentSpec.config().isClustered()) {
            locator = ActiveMQClient.createServerLocatorWithHA(connector);
        } else {
            locator = ActiveMQClient.createServerLocatorWithoutHA(connector);
        }
        // TODO review how this actually works under the bonnet, looks fishy!

        return new ServerConnector(locator);
    }

    public void stop() throws Exception {
        instance.stop();
    }

}
/* NOTE. Why not use EmbeddedActiveMQ?!
 * I wanted to have more flexible configuration where you could merge external
 * settings in "broker.xml" with programmatic config---i.e. direct manipulation
 * of the core config object. (This way you can e.g. read most settings from
 * the XML file and then add some tricky bits programmatically.)
 * So I had to strip the code that parses "broker.xml" out of EmbeddedActiveMQ
 * and put it into my own CoreConfifFactory. But then there wasn't much code
 * left in EmbeddedActiveMQ, so I decided to roll out my own helper class to
 * improve their design a bit. (EmbeddedActiveMQ is not a value object; it lets
 * you set parameters after starting the server which makes no sense as those
 * params won't be used; no pre-configured embedded endpoints; etc.)
 */
package kew.providers.artemis.runtime;

import static java.util.Objects.requireNonNull;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.api.core.client.ActiveMQClient;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.api.core.client.ClientSessionFactory;
import org.apache.activemq.artemis.api.core.client.ServerLocator;
import org.apache.activemq.artemis.core.server.ActiveMQServer;
import org.apache.activemq.artemis.core.server.impl.ActiveMQServerImpl;

import kew.providers.artemis.ServerConnector;
import util.lambda.FunctionE;

/**
 * Embeds an Artemis server instance in the current process.
 */
public class EmbeddedServer {

    /**
     * Starts an Artemis server.
     * @param spec how to configure the server for runtime deployment.
     * @return the embedded server.
     * @throws Exception if an error occurs while starting the server.
     * @throws NullPointerException if the argument is {@code null}.
     */
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

    /**
     * Starts a default, non-authenticated client session with this embedded
     * server.
     * @return the server connector which holds the session.
     * @throws Exception if an error occurs while starting the session.
     */
    public ServerConnector startClientSession() throws Exception {
        return startClientSession(ClientSessions.defaultSession());
    }

    /**
     * Starts a client session with this embedded server.
     * @param sessionFactory a factory to instantiate the Artemis session.
     * @return the server connector which holds the session.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws Exception if an error occurs while starting the session.
     */
    public ServerConnector startClientSession(
            FunctionE<ClientSessionFactory, ClientSession> sessionFactory)
                throws Exception {
        requireNonNull(sessionFactory, "sessionFactory");

        TransportConfiguration connector = deploymentSpec.embeddedEndpoints()
                                                         .connector()
                                                         .transport();
        ServerLocator locator =
                ActiveMQClient.createServerLocatorWithoutHA(connector);  // (*)

        return new ServerConnector(locator, sessionFactory);
    }
    /* (*) Even if the server is clustered we don't need HA anyway cos the
     * client lives in the same process as the server and there's no network
     * connection in between. If the process goes down both server and client
     * die anyway. Also, we can obviously connect the locator to the server
     * reliably using the embedded server connector---i.e. no need to use a
     * static list of connectors or dynamic discovery even if the server was
     * clustered. See notes below about server locators and cluster topology.
     */

    /**
     * Stops the server.
     * @throws Exception if an error occurs while stopping the server.
     */
    public void stop() throws Exception {
        instance.stop();
    }

    /**
     * @return the underlying Artemis server instance.
     */
    public ActiveMQServer instance() {
        return instance;
    }

}
/* NOTES
 * -----
 * 1. Why not use EmbeddedActiveMQ?!
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
 *
 * 2. Server Locator & Cluster Topology.
 * The server locator is used by an Artemis client to connect to the server.
 * In the case of a cluster, you'd typically want the locator to know about
 * cluster topology so that it can help with high availability and/or load
 * balancing---e.g. if one cluster member is down the locator can transparently
 * connect to another member. (Note however that even if the locator is not
 * aware of the cluster topology and always connects to the same server, Artemis
 * will still load-balance messages among cluster members.)
 * How does the locator get to know about cluster topology? It first needs to
 * be able to locate some of the cluster members. This can be done either
 * + statically: you specify a list of connectors to some of the members; or
 * + dynamically: you use a discovery group to retrieve connector info broadcast
 * by cluster members---this only works if you have configured the cluster to
 * broadcast connectors either through UDP or JGroups.
 * Once the locator has a list of connectors to some of the cluster members,
 * it can try to connect to one of them to download the cluster topology. It
 * then automatically gets topology updates as it connects to the cluster
 * members during normal operation---e.g. the sending/receiving of messages.
 */

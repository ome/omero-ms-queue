package kew.providers.artemis.runtime;

import static java.util.Objects.requireNonNull;
import static util.sequence.Streams.pruneNull;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.activemq.artemis.core.client.impl.Topology;
import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.server.ActiveMQServer;
import org.apache.activemq.artemis.core.server.cluster.ClusterConnection;
import org.apache.activemq.artemis.core.server.cluster.ClusterManager;

import util.lambda.FunctionE;
import util.types.PositiveN;

/**
 * Park your threads here while you're waiting for other Artemis instances to
 * join the cluster your embedded server is in.
 */
public class ClusterWaitingRoom {

    private static void checkServerIsConfiguredToBeInACluster(
            ActiveMQServer clusterMember) {
        boolean isMember = Optional.ofNullable(clusterMember.getConfiguration())
                                   .map(Configuration::isClustered)
                                   .orElse(false);
        if (!isMember) {
            throw new IllegalArgumentException(
                    "Artemis instance not configured to be in a cluster!");
        }
    }

    private final ActiveMQServer clusterMember;

    /**
     * Creates a new instance.
     * @param clusterMember an Artemis sever that's configured to be a member
     *                     of the cluster you want to wait on forming.
     * @throws IllegalArgumentException if the argument is not configured to be
     * a member of a cluster.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public ClusterWaitingRoom(ActiveMQServer clusterMember) {
        requireNonNull(clusterMember, "clusterMember");
        checkServerIsConfiguredToBeInACluster(clusterMember);

        this.clusterMember = clusterMember;
    }

    private Stream<ClusterConnection> clusterConnections() {
        return Optional.ofNullable(clusterMember.getClusterManager())
                       .map(ClusterManager::getClusterConnections)
                       .map(Collection::stream)
                       .orElseGet(Stream::empty);
    }

    private Optional<Integer> countTopologyMembers(ClusterConnection c) {
        return Optional.ofNullable(c)
                       .map(ClusterConnection::getTopology)
                       .map(Topology::getMembers)
                       .map(Collection::size);
    }

    private Stream<Integer> countClusterMembersOnEachConnection() {
        return clusterConnections()
              .map(this::countTopologyMembers)
              .filter(Optional::isPresent)
              .map(Optional::get);
    }

    /**
     * Does the cluster currently have at least {@code min} members?
     * @param min the expected minimum number of cluster members.
     * @return {@code true} for yes, {@code false} for no.
     */
    public boolean hasMinMembersOf(PositiveN min) {
        return countClusterMembersOnEachConnection()
              .map(n -> min.get() <= n)
              .findAny()
              .orElse(false);
    }
    /* NOTE. Laziness.
     * The check will succeed on retrieving the first Topology which has at
     * least min members. This potentially avoids downloading all Topology
     * objects from each configured cluster connection.
     */

    /**
     * Waits for the cluster to have at least the specified number of members.
     * Cluster size is checked at the specified wait intervals until the size
     * is no smaller then the specified threshold or the check has been done
     * {@code n} times, where {@code n} is the number of wait intervals.
     * @param serversThreshold how many members at least should've joined the
     *                         cluster.
     * @param waitIntervals intervals at which to check for cluster size. This
     *                      method does nothing and returns {@code false} if
     *                      the stream is empty of all its values are {@code
     *                      null}.
     * @return {@code true} if the cluster has at least the specified number
     * of members; {@code false} if, even after all the waiting, the cluster
     * size is still smaller than the specified threshold.
     * @throws NullPointerException if any argument is {@code null}.
     * <p>The following checked exceptions are rethrown as unchecked (i.e.
     * the exception is masked as a runtime exception and thrown as is without
     * wrapping it in a {@code RuntimeException}):
     * <br>{@link InterruptedException} if the current thread is interrupted
     * while sleeping.
     * </p>
     */
    public boolean waitForClusterForming(PositiveN serversThreshold,
                                         Stream<Duration> waitIntervals) {
        requireNonNull(serversThreshold, "serversThreshold");
        requireNonNull(waitIntervals, "waitIntervals");

        FunctionE<Duration, Boolean> delayedCheck = d -> {
            Thread.sleep(d.toMillis());  // can throw InterruptedException
            return hasMinMembersOf(serversThreshold);
        };
        return pruneNull(waitIntervals)
              .map(delayedCheck)
              .findAny()
              .orElse(false);
    }

}
/* NOTES.
 * 1. EmbeddedActiveMQ.
 * The functionality here is pretty similar to the waitClusterForming method
 * of EmbeddedActiveMQ. But I have made their code more robust (checking for
 * *all* possible null pointers), rewritten it in functional style, and made
 * it more flexible---wait intervals can have different lengths; has*MinOf()
 * lets you check cluster size without delays.
 */
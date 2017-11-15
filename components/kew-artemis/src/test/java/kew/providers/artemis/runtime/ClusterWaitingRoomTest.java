package kew.providers.artemis.runtime;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static util.sequence.Arrayz.array;

import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.server.ActiveMQServer;
import org.junit.Before;
import org.junit.Test;
import util.types.PositiveN;

import java.time.Duration;
import java.util.stream.Stream;


public class ClusterWaitingRoomTest extends ClusterWaitingRoom {

    /* Ideally we should be using the code below, but the Topology and
     * ClusterManager classes are final and our version of Mockito can't
     * mock them. Mockito 2 supports mocking final classes though, so if
     * we upgrade we should add the code below to these unit tests.
     */
    /* Start of Mockito 2 code.

    private static ClusterConnection mockTopology(int clusterSize) {
        List<TopologyMemberImpl> members =
                IntStream.range(0, clusterSize)
                         .mapToObj(x -> mock(TopologyMemberImpl.class))
                         .collect(Collectors.toList());
        ClusterConnection conn = mock(ClusterConnection.class);
        Topology topology = mock(Topology.class);
        when(topology.getMembers()).thenReturn(members);
        when(conn.getTopology()).thenReturn(topology);

        return conn;
    }

    private static ActiveMQServer mockClusterConnections(int...clusterSizes) {
        Set<ClusterConnection> cs =
                IntStream.of(clusterSizes)
                         .mapToObj(ClusterWaitingRoomTest::mockTopology)
                         .collect(Collectors.toSet());

        Configuration clusteredConfig = mock(Configuration.class);
        when(clusteredConfig.isClustered()).thenReturn(true);
        ClusterManager manager = mock(ClusterManager.class);
        when(manager.getClusterConnections()).thenReturn(cs);

        ActiveMQServer server = mock(ActiveMQServer.class);
        when(server.getConfiguration()).thenReturn(clusteredConfig);
        when(server.getClusterManager()).thenReturn(manager);

        return server;
    }

    @DataPoints
    public static final Integer[] clusterSizeSupply = array(0, 1, 2, 3);

    @DataPoints
    public static final PositiveN[] thresholdSupply = array(
            PositiveN.of(1), PositiveN.of(2), PositiveN.of(4));

    @Theory
    public void waitForClusterAgreesWithHasMinMembers(
            PositiveN serversThreshold,
            Integer clusterSize1, Integer clusterSize2, Integer clusterSize3) {
        ActiveMQServer clusterMember = mockClusterConnections(
                clusterSize1, clusterSize2, clusterSize3);
        ClusterWaitingRoom target = new ClusterWaitingRoom(clusterMember);

        boolean waitForClusterAnswer = target.waitForClusterForming(
                serversThreshold, Stream.of(Duration.ofMillis(1),
                                            Duration.ofMillis(1)));
        boolean hasMinMembersAnswer = target.hasMinMembersOf(serversThreshold);

        assertThat(waitForClusterAnswer, is(hasMinMembersAnswer));
    }

    */  // end of Mockito 2 code.

    private static ActiveMQServer newServer(boolean clustered) {
        Configuration config = mock(Configuration.class);
        when(config.isClustered()).thenReturn(clustered);

        ActiveMQServer server = mock(ActiveMQServer.class);
        when(server.getConfiguration()).thenReturn(config);

        return server;
    }

    Stream<Integer>[] topologyQueries;
    int toplogyQueriesCount;

    public ClusterWaitingRoomTest() {
        super(newServer(true));
    }

    @Before
    public void setup() {
        topologyQueries = null;
        toplogyQueriesCount = 0;
    }

    @Override
    protected Stream<Integer> countClusterMembersOnEachConnection() {
        if (++toplogyQueriesCount <= topologyQueries.length) {
            return topologyQueries[toplogyQueriesCount - 1];
        }
        return Stream.empty();
    }

    @Test
    public void hasMinMembersOfReturnsTrueIfExistsConnWithAtLeastMinMembers() {
        topologyQueries = array(Stream.of(1, 2, 1, 3));
        boolean actual = hasMinMembersOf(PositiveN.of(2));
        assertTrue(actual);
    }

    @Test
    public void hasMinMembersOfReturnsFalseIfNoConnHasAtLeastMinMembers() {
        topologyQueries = array(Stream.of(1, 2, 1, 3));
        boolean actual = hasMinMembersOf(PositiveN.of(4));
        assertFalse(actual);
    }

    @Test
    public void waitForClusterFormingTriesUntilThresholdReached() {
        topologyQueries = array(
                Stream.empty(), Stream.of(1, 1, 1, 1), Stream.of(1, 2, 1, 3));
        boolean actual = waitForClusterForming(
                            PositiveN.of(3),
                            Stream.of(Duration.ofMillis(1),
                                      Duration.ofMillis(1),
                                      Duration.ofMillis(1),
                                      Duration.ofMillis(1)));
        assertTrue(actual);
        assertThat(toplogyQueriesCount, is(3));
    }

    @Test
    public void waitForClusterFormingGivesUpAfterAllIntervalsElapsed() {
        topologyQueries = array(
                Stream.empty(), Stream.of(1, 1), Stream.of(1, 1));
        boolean actual = waitForClusterForming(
                            PositiveN.of(3),
                            Stream.of(Duration.ofMillis(1),
                                      Duration.ofMillis(1),
                                      Duration.ofMillis(1),
                                      Duration.ofMillis(1)));
        assertFalse(actual);
        assertThat(toplogyQueriesCount, is(4));
    }

    @Test
    public void hasMinMembersOfNeverThrowsEvenIfArtemisReturnsNulls() {
        ClusterWaitingRoom target = new ClusterWaitingRoom(newServer(true));
        boolean actual = target.hasMinMembersOf(PositiveN.of(1));

        assertFalse(actual);
    }

    @Test
    public void waitForClusterFormingNeverThrowsEvenIfArtemisReturnsNulls() {
        ClusterWaitingRoom target = new ClusterWaitingRoom(newServer(true));
        boolean actual = target.waitForClusterForming(
                PositiveN.of(1),
                Stream.of(Duration.ofMillis(1), Duration.ofMillis(1)));

        assertFalse(actual);
    }

    @Test (expected = IllegalArgumentException.class)
    public void ctorThrowsIfServerNotConfiguredAsClusterMember() {
        new ClusterWaitingRoom(newServer(false));
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullServer() {
        new ClusterWaitingRoom(null);
    }

}

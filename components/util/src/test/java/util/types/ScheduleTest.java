package util.types;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.object.Pair.pair;
import static util.sequence.Arrayz.array;
import static util.types.FutureTimepoint.now;

import util.object.Pair;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.time.Duration;

@RunWith(Theories.class)
public class ScheduleTest {

    @DataPoints
    public static final String[] whatSupply = array("", "a", "ab");

    @DataPoints
    public static final FutureTimepoint[] whenSupply =
            array(now(), new FutureTimepoint(Duration.ofMinutes(1)));

    @Theory
    public void equalityAgreesWithHashing(
            FutureTimepoint when1, String what1,
            FutureTimepoint when2, String what2) {
        Schedule<String> s1 = new Schedule<>(when1, what1);
        Schedule<String> s2 = new Schedule<>(when2, what2);

        if (when1.equals(when2) && what1.equals(what2)) {
            assertTrue(s1.equals(s2));
            assertTrue(s2.equals(s1));
            assertThat(s1.hashCode(), is(s2.hashCode()));
        } else {
            assertFalse(s1.equals(s2));
            assertFalse(s2.equals(s1));
            assertThat(s1.hashCode(), is(not(s2.hashCode())));
        }
    }

    @Test
    public void equalsReturnsFalseIfNullInput() {
        Schedule<String> target = new Schedule<>(now(), "");
        assertFalse(target.equals(null));
    }

    @Test
    public void equalsReturnsTrueIfSameRef() {
        Schedule<String> target = new Schedule<>(now(), "");
        assertTrue(target.equals(target));
    }

    @Test
    public void equalsReturnsFalseIfDifferentType() {
        Pair<FutureTimepoint, String> state = pair(now(), "");
        Schedule<String> target = new Schedule<>(state.fst(), state.snd());
        assertFalse(target.equals(state));
    }

    @Test
    public void equalsReturnsFalseIfDifferentState() {
        Pair<FutureTimepoint, String> state = pair(now(), "");
        Schedule<String> target = new Schedule<>(state.fst(), state.snd());
        assertFalse(target.equals(state));
    }

    @Test
    public void toStringFormatsStateAsPair() {
        Schedule<Integer> schedule = new Schedule<>(now(), 1);
        String target = schedule.toString();

        assertNotNull(target);
        assertThat(target, startsWith("("));
        assertThat(target, endsWith(", 1)"));
    }

    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfNullWhen() {
        new Schedule<>(null, 1);
    }

    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfNullWhat() {
        new Schedule<>(now(), null);
    }
}

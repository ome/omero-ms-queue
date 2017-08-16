package util.object;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.object.Pair.pair;
import static util.sequence.Arrayz.array;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.Objects;


@RunWith(Theories.class)
public class PairTest {

    @DataPoints
    public static final String[] fstSupply = array("", "a", "ab");

    @DataPoints
    public static final Integer[] sndSupply = array(0, 1, 12);

    @Theory
    public void equalityAgreesWithHashing(
            String fst1, Integer snd1,
            String fst2, Integer snd2) {
        Pair<String, Integer> p1 = pair(fst1, snd1);
        Pair<String, Integer> p2 = pair(fst2, snd2);

        if (Objects.equals(fst1, fst2) && Objects.equals(snd1, snd2)) {
            assertTrue(p1.equals(p2));
            assertTrue(p2.equals(p1));
            assertThat(p1.hashCode(), is(p2.hashCode()));
        } else {
            assertFalse(p1.equals(p2));
            assertFalse(p2.equals(p1));
            assertThat(p1.hashCode(), is(not(p2.hashCode())));  // (*)
        }
    }
    /* (*) Hash Collisions.
     * We have excluded nulls from the data points above to avoid collisions.
     * In fact, Objects.hash() converts nulls to 0, so e.g. ("x", null) and
     * ("x", 0) have the same hash even though they're not equal. Ditto for
     * the null and empty string:
     *                            ("", 1) != (null, 1)
     * but
     *                Objects.hash("", 1) == Objects.hash(null, 1)
     */

    @Test
    public void equalsReturnsFalseIfNullInput() {
        Pair<String, Integer> target = pair("x", 1);
        assertFalse(target.equals(null));
    }

    @Test
    public void equalsReturnsTrueIfSameRef() {
        Pair<String, Integer> target = pair("x", 1);
        assertTrue(target.equals(target));
    }

    @Test
    public void equalsReturnsFalseIfDifferentType() {
        assertFalse(pair(null, 1).equals(1));
        assertFalse(pair("", null).equals(""));
    }

    @Test
    public void toStringFormatsStateAsPair() {
        String target = pair("x", 1).toString();

        assertNotNull(target);
        assertThat(target, startsWith("("));
        assertThat(target, endsWith(", 1)"));
    }

    @Test
    public void ctorAcceptsNulls() {
        new Pair<String, Integer>(null, null);
    }

}

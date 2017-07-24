package util.types;

import static org.hamcrest.Matchers.*;
import static org.junit.Assume.*;
import static org.junit.Assert.*;
import static util.object.Pair.pair;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import util.object.Either;
import util.object.Pair;

import java.util.stream.Stream;

@RunWith(Theories.class)
public class EmailTest {

    @SuppressWarnings("unchecked")
    private static Pair<Boolean, String>[] buildSupply(
            boolean valid, String...xs) {
        return Stream.of(xs)
                     .map(v -> pair(valid, v))
                     .toArray(Pair[]::new);
    }

    @DataPoints
    public static final Pair<Boolean, String>[] invalidAddressSupply =
        buildSupply(false,
                null, "", " ", "a", "ab", "a b @ c", "@", "@some.edu", "me@");

    @DataPoints
    public static final Pair<Boolean, String>[] validAddressSupply =
        buildSupply(true,
                "x@y", "x.y@z", "x@y.z", "x.y@w.z");

    private static void assertFromRejects(String value) {
        Either<String, Email> actual = Email.from(value);

        assertNotNull(actual);
        assertTrue(actual.isLeft());
        assertThat(actual.getLeft(), containsString("invalid"));
    }

    private static void assertOfThrows(String value) {
        try {
            Email.of(value);
            fail("should have thrown: " + value);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), containsString("invalid"));
        }
    }

    private static Email assertFromAccepts(String value) {
        Either<String, Email> actual = Email.from(value);

        assertNotNull(actual);
        assertTrue(actual.isRight());
        assertNotNull(actual.getRight());

        return actual.getRight();
    }

    @Theory
    public void fromAndOfAgreeOnInvalidValues(Pair<Boolean, String> p) {
        assumeThat(p.fst(), is(false));

        String value = p.snd();
        assertFromRejects(value);
        assertOfThrows(value);
    }

    @Theory
    public void fromAndOfAgreeOnValidValues(Pair<Boolean, String> p) {
        assumeThat(p.fst(), is(true));

        String value = p.snd();
        Email fromValue = assertFromAccepts(value);
        Email ofValue = Email.of(value);

        assertThat(fromValue, is(ofValue));
    }

}

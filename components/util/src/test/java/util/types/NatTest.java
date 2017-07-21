package util.types;

import static util.sequence.Arrayz.array;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class NatTest {

    @DataPoints 
    public static final Long[] supply = array(
            Long.MIN_VALUE, -10L, -1L, 0L, 1L, 10L, Long.MAX_VALUE); 
    
    @Theory
    public void failIfNegativeValue(Long negative) {
        assumeThat(negative, lessThan(0L));
        try {
            Nat.of(negative);
            fail("expected " + IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            // expected
        }
    }
    
    @Theory
    public void buildIfNonNegativeValue(Long expected) {
        assumeThat(expected, greaterThanOrEqualTo(0L));
        Nat actual = Nat.of(expected);
        assertThat(actual, is(expected));
    }
    
    @Theory
    public void maybeBuild(Long value) {
        Optional<Nat> expected = Nat.isValid.test(value) ? 
                Optional.of(Nat.of(value)) : Optional.empty();
        Optional<Nat> actual = Nat.from(value);
        assertThat(actual, is(expected));
    }
    
}

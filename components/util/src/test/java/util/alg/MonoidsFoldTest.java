package util.alg;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class MonoidsFoldTest {

    @DataPoints
    public static final Integer[] values = MonoidProviderTest.values;
    
    private Monoids<Integer> sumMonoid;
    
    @Before
    public void setup() {
        sumMonoid = Monoids.with(MonoidProviderTest.intSumMonoid());
    }
    
    @Theory
    public void fold(Integer x, Integer y, Integer z) {
        Integer actual = sumMonoid.fold(Stream.of(x, y, z));
        assertThat(actual, is(x + y + z));
    }
    
    @Test
    public void foldOfEmptyIsUnit() {
        Integer actual = sumMonoid.fold(Stream.empty());
        assertThat(actual, is(0));
    }
    
    @Test (expected = NullPointerException.class)
    public void foldThrowsIfNullArg() {
        sumMonoid.fold(null);
    }
    
    @Test (expected = NullPointerException.class)
    public void foldThrowsIfStreamContainsNull() {
        sumMonoid.fold(Stream.of(1, null, 3));
    }
    
    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullArg() {
        new Monoids<>(null);
    }
    
}

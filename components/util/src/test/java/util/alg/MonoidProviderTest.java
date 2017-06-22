package util.alg;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.sequence.Arrayz.array;

import java.util.function.BinaryOperator;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class MonoidProviderTest {

    public static final Supplier<Integer> zero = () -> 0;
    public static final BinaryOperator<Integer> sum = (x, y) -> x + y;
    
    public static Monoid<Integer> intSumMonoid() {
        return new MonoidProvider<>(sum, zero);
    }
    
    @DataPoints
    public static final Integer[] values = array(-2, -1, 0, 1, 2, 3, 4);  
    
    
    private Monoid<Integer> sumMonoid;
    
    @Before
    public void setup() {
        sumMonoid = intSumMonoid();
    }
    
    @Theory
    public void sumBinOp(Integer x, Integer y) {
        Integer actual = sumMonoid.multiply(x, y);
        assertThat(actual, is(x + y));
    }
    
    @Theory
    public void unitLaw(Integer x) {
        Integer left = sumMonoid.multiply(0, x);
        Integer right = sumMonoid.multiply(x, 0);
        
        assertThat(left, is(x));
        assertThat(right, is(x));
    }
    
    @Test
    public void sumUnitIsZero() {
        assertThat(sumMonoid.unit(), is(0));
    }
    
    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullFirstArg() {
        new MonoidProvider<>(null, zero);
    }
    
    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullSecondArg() {
        new MonoidProvider<>(sum, null);
    }
    
}

package util.lambda;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static java.util.function.Function.identity;
import static util.lambda.Functions.constant;
import static util.sequence.Arrayz.array;

import java.util.function.Function;
import java.util.stream.IntStream;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class FunctionsConstantTest {

    @DataPoints
    public static int[] inputs = IntStream.range(-10, 10).toArray();
    
    @DataPoints
    public static String[] constants = array("", "a", "ab", "abc");
    
    @Theory
    public void alwaysReturnGivenValue(int x, String c) {
        Function<Integer, String> k = constant(c);
        assertThat(k.apply(x), is(c));
    }
    
    @Theory
    public void alwaysReturnGivenValueEvenIfLambda(int x, String c) {
        Function<String, String> id = identity();
        Function<Integer, Function<String, String>> k = constant(id);
        
        assertThat(k.apply(x), is(id));
        assertThat(k.apply(x).apply(c), is(c));
    }
    
}

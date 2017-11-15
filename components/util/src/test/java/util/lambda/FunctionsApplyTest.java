package util.lambda;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static java.util.function.Function.identity;
import static util.lambda.Functions.apply;
import static util.sequence.Arrayz.array;

import java.util.function.Function;
import java.util.stream.IntStream;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class FunctionsApplyTest {

    @DataPoints
    public static int[] inputs = IntStream.range(-10, 10).toArray();

    @DataPoints
    public static Function<Integer, Integer>[] functions =
            array(identity(), x -> x + 1);

    @Theory
    public void sameAsBuiltInLambdaApply(Function<Integer, Integer> f, int x) {
        assertThat(f.apply(x), is(apply(f, x)));
    }

    @Theory
    public void sameAsBuiltInLambdaApplyEvenOnNullInput(
            Function<Integer, Integer> f) {
        boolean fThrows = false, applyThrows = false;
        // NB identity doesn't throw, (+1) does.
        try {
            f.apply(null);
        } catch (NullPointerException e) {
            fThrows = true;
        }
        try {
            apply(f, null);
        } catch (NullPointerException e) {
            applyThrows = true;
        }
        assertThat(fThrows, is(applyThrows));
    }

    @Test(expected = NullPointerException.class)
    public void throwIfNullFunction() {
        apply(null, 1);
    }

    @Test
    public void ctor() {
        new Functions();  // only to get 100% coverage.
    }

}

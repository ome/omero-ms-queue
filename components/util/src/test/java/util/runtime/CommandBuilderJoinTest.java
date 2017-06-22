package util.runtime;

import static util.runtime.CommandLineBuilderTest.concat;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.stream.Stream;

import static org.junit.Assert.assertArrayEquals;

@RunWith(Theories.class)
public class CommandBuilderJoinTest {

    private static CommandBuilder newBuilder(String...xs) {
        return () -> Stream.of(xs);
    }

    @DataPoints
    public static final String[][] tokensSupply =
            CommandLineBuilderTest.tokensSupply;

    @Theory
    public void emptyBuilderAsLeftIdentity(String[] xs) {
        CommandBuilder z = newBuilder(xs);
        CommandBuilder id = CommandBuilder.empty();
        CommandBuilder joined = z.join(id);

        String[] actual = joined.tokens().toArray(String[]::new);
        assertArrayEquals(xs, actual);
    }

    @Theory
    public void emptyBuilderAsRightIdentity(String[] xs) {
        CommandBuilder x = newBuilder(xs);
        CommandBuilder id = CommandBuilder.empty();
        CommandBuilder joined = id.join(x);

        String[] actual = joined.tokens().toArray(String[]::new);
        assertArrayEquals(xs, actual);
    }

    @Theory
    public void joinAppendsOtherSequenceOfTokens(String[] xs, String[] ys) {
        CommandBuilder x = newBuilder(xs);
        CommandBuilder y = newBuilder(ys);
        CommandBuilder joined = x.join(y);

        String[] actual = joined.tokens().toArray(String[]::new);
        String[] expected = concat(xs, ys);
        assertArrayEquals(expected, actual);
    }

    @Test(expected = NullPointerException.class)
    public void throwIfNullArg() {
        newBuilder().join(null);
    }

}

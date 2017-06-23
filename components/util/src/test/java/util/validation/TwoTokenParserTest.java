package util.validation;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.object.Pair.pair;
import static util.sequence.Arrayz.array;
import static util.validation.ParserFactory.*;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import util.object.Either;
import util.object.Pair;

@RunWith(Theories.class)
public class TwoTokenParserTest {

    private static ObjectParser<Pair<Integer, String>> parser() {
        return pairParser(intParser(), stringParser());
    }
    
    private static void assertLeft(Either<String, Pair<Integer, String>> parseResult) {
        assertNotNull(parseResult);
        assertTrue(parseResult.isLeft());
    }
    
    private static void leftOnNullOrEmptyInput(boolean useNull) {
        String token = useNull ? null : "";
        assertLeft(parser().parse(token));
    }
    
    @DataPoints
    public static String[][] tokensSupply = new String[][] {
        array("1", "2"), array("1", null, "3"), array("*", "2")
    };
    
    @Theory
    public void combinedParserIsSequencingOfParsers(String[] tokens) {
        Either<String, Integer> fstResult = intParser().parse(tokens[0]);
        Either<String, String> sndResult = stringParser().parse(tokens[1]);
        Either<String, Pair<Integer, String>> parseResult = parser().parse(tokens);
        
        assertNotNull(parseResult);
        if (fstResult.isLeft()) {
            assertTrue(parseResult.isLeft());
            assertThat(parseResult.getLeft(), is(fstResult.getLeft()));
        } else {
            if (sndResult.isLeft()) {
                assertTrue(parseResult.isLeft());
                assertThat(parseResult.getLeft(), is(sndResult.getLeft()));
            } else {
                assertTrue(parseResult.isRight());
                assertThat(parseResult.getRight(), 
                           is(pair(fstResult.getRight(), sndResult.getRight())));
            }
        }
    }
    
    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullFstArg() {
        new TwoTokenParser<>(null, stringParser());
    }
    
    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullSndArg() {
        new TwoTokenParser<>(intParser(), null);
    }
    
    @Test
    public void parseReturnsLeftOnNullOrEmptyInput() {
        leftOnNullOrEmptyInput(true);
        leftOnNullOrEmptyInput(false);
    }
    
    @Test
    public void parseReturnsLeftIfOneToken() {
        assertLeft(parser().parse("1"));
    }
    
    @Test
    public void parseIgnoresAnyTokenPastTheThird() {
        String[] tokens = array("1", "y", "3");
        Either<String, Pair<Integer, String>> parseResult = parser().parse(tokens);
        
        assertNotNull(parseResult);
        assertTrue(parseResult.isRight());
        assertThat(parseResult.getRight(), is(pair(1, "y")));
    }
    
}

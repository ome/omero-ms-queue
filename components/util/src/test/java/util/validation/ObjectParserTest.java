package util.validation;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.sequence.Arrayz.array;

import java.util.stream.Stream;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import util.object.Either;

@RunWith(Theories.class)
public class ObjectParserTest {
    
    @DataPoints
    public static String[][] tokensSupply = new String[][] {
        array(), array("1"), array((String)null), array("1", "2"), 
        array("1", null, "3")
    };
    
    private static 
    void assertIdentity(String[] tokens, Either<String, Stream<String>> parseResult) {
        assertNotNull(parseResult);
        assertTrue(parseResult.isRight());
        
        String[] parsed = parseResult.getRight().toArray(String[]::new);
        assertArrayEquals(tokens, parsed);
    }
    
    @Theory
    public void identityParserReturnsTokensAsTheyAre(String[] tokens) {
        ObjectParser<Stream<String>> identity = ParserFactory.identityParser();
        
        assertIdentity(tokens, identity.parse(tokens));
        assertIdentity(tokens, identity.parse(Stream.of(tokens)));
    }

    @Theory
    public void identityParserReturnsTokensAsTheyAreIfValidationSucceeds(String[] tokens) {
        ObjectParser<Stream<String>> p = ParserFactory
                                        .identityParser()
                                        .withValidation(Either::right);
        
        assertIdentity(tokens, p.parse(tokens));
    }
    
    @Theory
    public void identityParserFailsIfValidationFails(String[] tokens) {
        String error = "error";
        ObjectParser<Stream<String>> p = ParserFactory
                                        .identityParser()
                                        .withValidation(x -> Either.left(error));
        Either<String, Stream<String>> parseResult = p.parse(tokens);
        
        assertNotNull(parseResult);
        assertTrue(parseResult.isLeft());
        assertThat(parseResult.getLeft(), is(error));
    }
    
    @Test (expected = NullPointerException.class)
    public void parseThrowsIfNullStream() {
        ObjectParser<Stream<String>> identity = ParserFactory.identityParser();
        assertNotNull(identity);
        identity.parse((Stream<String>)null);
    }
    
    @Test (expected = NullPointerException.class)
    public void parseThrowsIfNullArray() {
        ObjectParser<Stream<String>> identity = ParserFactory.identityParser();
        assertNotNull(identity);
        identity.parse((String[])null);
    }
    
    
}

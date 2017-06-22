package util.validation;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.sequence.Arrayz.array;

import java.util.function.Function;

import org.junit.Test;

import util.lambda.FunctionE;
import util.object.Either;

public class SingleTokenParserAdapterTest {
    
    private static void leftOnNullOrEmptyInput(boolean useNull) {
        String error = "ouch!";
        FunctionE<String, Object> fail = x -> { throw new Exception(error); };
        SingleTokenParserAdapter<Object> target = 
                new SingleTokenParserAdapter<>(fail);
        
        String token = useNull ? null : "";
        Either<String, Object> parseResult = target.parse(token);
        
        assertNotNull(parseResult);
        assertTrue(parseResult.isLeft());
        assertThat(parseResult.getLeft(), is(not(error)));
    }
    
    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullFunction() {
        new SingleTokenParserAdapter<>((Function<String, Object>)null);
    }
    
    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullFunctionE() {
        new SingleTokenParserAdapter<>(null);
    }
    
    @Test
    public void parseReturnsLeftOnNullOrEmptyInput() {
        leftOnNullOrEmptyInput(true);
        leftOnNullOrEmptyInput(false);
    }
    
    @Test
    public void parseReturnsLeftOnException() {
        String error = "ouch!";
        FunctionE<String, Object> fail = x -> { throw new Exception(error); };
        SingleTokenParserAdapter<Object> target = 
                new SingleTokenParserAdapter<>(fail);
        
        Either<String, Object> parseResult = target.parse("x");
        assertNotNull(parseResult);
        assertTrue(parseResult.isLeft());
        assertThat(parseResult.getLeft(), is(error));
    }
    
    @Test
    public void parseIgnoresAnyTokenPastTheFirst() {
        String[] tokens = array("x", "y");
        Either<String, String> parseResult = ParserFactory
                                            .stringParser()
                                            .parse(tokens);
        assertNotNull(parseResult);
        assertTrue(parseResult.isRight());
        assertThat(parseResult.getRight(), is(tokens[0]));
    }
    
    @Test
    public void parseValidTokenButInvalidValueFails() {
        String[] tokens = array("-1", "0");
        Either<String, Integer> parseResult = ParserFactory
                                             .positiveIntParser()
                                             .parse(tokens);
        assertNotNull(parseResult);
        assertTrue(parseResult.isLeft());
    }
    
    @Test
    public void parseValidTokenWithValidValueSucceeds() {
        Either<String, Integer> parseResult = ParserFactory
                                             .positiveIntParser()
                                             .parse("1", "-1");
        assertNotNull(parseResult);
        assertTrue(parseResult.isRight());
        assertThat(parseResult.getRight(), is(1));
    }
}

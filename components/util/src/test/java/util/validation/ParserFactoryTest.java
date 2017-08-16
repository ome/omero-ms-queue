package util.validation;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.object.Pair.pair;

import org.junit.Test;
import util.object.Either;
import util.object.Pair;

import java.net.URI;


public class ParserFactoryTest {

    private static <T> void parseAndAssert(ObjectParser<T> parser, T expected) {
        Either<String, T> result = parser.parse(expected.toString());

        assertNotNull(result);
        assertTrue(result.isRight());
        assertThat(result.getRight(), is(expected));
    }

    @Test
    public void parseString() {
        parseAndAssert(ParserFactory.stringParser(), "xxx");
    }

    @Test
    public void parseInt() {
        parseAndAssert(ParserFactory.intParser(), -2);
    }

    @Test
    public void parsePositiveInt() {
        parseAndAssert(ParserFactory.positiveIntParser(), 2);
    }

    @Test
    public void parseLong() {
        parseAndAssert(ParserFactory.longParser(), -2L);
    }

    @Test
    public void parsePositiveLong() {
        parseAndAssert(ParserFactory.positiveLongParser(), 2L);
    }

    @Test
    public void parsePair() {
        ObjectParser<Pair<Integer, String>> parser =
                ParserFactory.pairParser(
                    ParserFactory.intParser(), ParserFactory.stringParser());
        Pair<Integer, String> expected = pair(2, "xxx");
        Either<String, Pair<Integer, String>> result =
                parser.parse(expected.fst().toString(), expected.snd());

        assertNotNull(result);
        assertTrue(result.isRight());
        assertThat(result.getRight(), is(expected));
    }

    @Test
    public void parseUri() {
        parseAndAssert(ParserFactory.uriParser(), URI.create("http://host"));
    }

    @Test
    public void parseFilePathUri() {
        ObjectParser<URI> parser = ParserFactory.filePathUriParser();
        Either<String, URI> result = parser.parse("/");

        assertNotNull(result);
        assertTrue(result.isRight());
    }

    @Test
    public void ctor() {
        new ParserFactory();  // only to get 100% coverage.
    }

}

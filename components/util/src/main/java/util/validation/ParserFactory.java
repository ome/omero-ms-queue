package util.validation;

import static java.util.function.Function.identity;
import static util.object.Either.left;
import static util.object.Either.right;

import java.net.URI;
import java.nio.file.Paths;
import java.util.stream.Stream;

import util.object.Either;
import util.object.Pair;

/**
 * Common parsers.
 */
public class ParserFactory {
    
    private static <T extends Number> Validator<String, T> checkPositive() {
        return parsed -> parsed.longValue() > 0 ? 
                                right(parsed) 
                              : left("not a positive integer: " + parsed);
    }
    
    /**
     * Builds a parser that returns the input stream as is, provided it is not
     * {@code null}, in which case a {@link NullPointerException} is thrown.
     * @return a no-op parser.
     */
    static ObjectParser<Stream<String>> identityParser() { 
        return Either::right;
    }

    /**
     * Builds a parser that accepts a string of length at least one and rejects
     * {@code null} and empty strings.
     * @return the parser.
     */
    public static ObjectParser<String> stringParser() {
        return new SingleTokenParserAdapter<>(identity());
    }
    
    /**
     * Builds a parser that accepts integers.
     * @return the parser.
     */
    public static ObjectParser<Integer> intParser() {
        return new SingleTokenParserAdapter<>(Integer::parseInt);
    }
    
    /**
     * Builds a parser that only accepts positive integers.
     * @return the parser.
     */
    public static ObjectParser<Integer> positiveIntParser() {
        return intParser().withValidation(checkPositive());
    }
    
    /**
     * Builds a parser that accepts longs.
     * @return the parser.
     */
    public static ObjectParser<Long> longParser() {
        return new SingleTokenParserAdapter<>(Long::parseLong);
    }
    
    /**
     * Builds a parser that only accepts positive longs.
     * @return the parser.
     */
    public static ObjectParser<Long> positiveLongParser() {
        return longParser().withValidation(checkPositive());
    }
    
    /**
     * Combines two parsers into one that applies the fist parser to the first
     * token and the second parser to the second token to produce a parsed pair.
     * @param <X> any type.
     * @param <Y> any type.
     * @param fst parses the first token.
     * @param snd parses the second token.
     * @return the parser.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static <X, Y> ObjectParser<Pair<X, Y>> pairParser(
            ObjectParser<X> fst, ObjectParser<Y> snd) {
        return new TwoTokenParser<>(fst, snd);
    }
    
    /**
     * Builds a parser that accepts {@link URI}'s.
     * @return the parser.
     */
    public static ObjectParser<URI> uriParser() {
        return new SingleTokenParserAdapter<>(URI::new);
    }
    
    /**
     * Builds a parser that accepts a file path and turns it into a "file:" 
     * {@link URI}'s. If the path is not absolute, it will be resolved against
     * the current working directory, i.e. using the {@code user.dir} JVM prop.
     * @return the parser.
     */
    public static ObjectParser<URI> filePathUriParser() {
        return new SingleTokenParserAdapter<>(x -> Paths.get(x).toUri());
    }
    
}

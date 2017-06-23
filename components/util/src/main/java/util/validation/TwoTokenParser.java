package util.validation;

import static java.util.Objects.requireNonNull;
import static util.object.Either.left;

import java.util.stream.Stream;

import util.object.Either;
import util.object.Pair;

/**
 * Combines two parsers into one that applies the fist parser to the first
 * token and the second parser to the second token to produce a parsed pair.
 */
public class TwoTokenParser<X, Y> implements ObjectParser<Pair<X, Y>> {

    private final ObjectParser<X> fstTokenParser;
    private final ObjectParser<Y> sndTokenParser;
    
    /**
     * Creates a new instance.
     * @param fstTokenParser parses the first token.
     * @param sndTokenParser parses the second token.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public TwoTokenParser(ObjectParser<X> fstTokenParser,
                          ObjectParser<Y> sndTokenParser) {
        requireNonNull(fstTokenParser, "fstTokenParser");
        requireNonNull(sndTokenParser, "sndTokenParser");
        
        this.fstTokenParser = fstTokenParser;
        this.sndTokenParser = sndTokenParser;
    }
    
    @Override
    public Either<String, Pair<X, Y>> parseNonNull(Stream<String> tokens) {
        String[] ts = tokens.toArray(String[]::new);
        int len = ts == null ? 0 : ts.length;
        if (len < 2) {
            return left("two tokens expected, found: " + len);
        }
        return fstTokenParser
              .parse(ts[0])
              .bind(x -> sndTokenParser.parse(ts[1])
                                       .map(y -> new Pair<>(x, y)));
    }

}
/* NOTE. Sooo lame!!!
 * So what if we wanted to sequence 3 parsers? And 4? Did I mention this is the
 * wrong approach to parsing in general? 
 * Also trying to combine more than two of these puppies in Java and be type 
 * safe at the same time is way more work than a single minion like me can 
 * possibly endure in a life time!
 */ 

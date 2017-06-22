package util.validation;

import static java.util.Objects.requireNonNull;

import java.util.stream.Stream;

import util.object.Either;

/**
 * Turns a textual representation of a {@code T}-value into an instance of 
 * {@code T}.
 */
public interface ObjectParser<T> {
        
    /**
     * Same as {@link #parse(Stream)} but doesn't check if the argument is
     * {@code null}. This is the only method you need to implement as you can
     * rely on the default implementation of {@link #parse(Stream)} to do the
     * check.
     * @param tokens the input tokens.
     * @return the parse result.
     */
    Either<String, T> parseNonNull(Stream<String> tokens);
    
    /**
     * Parses a textual representation of a {@code T}-value.
     * @param tokens the tokens that make up the state of a {@code T}-value,
     * typically {@code T}'s fields. Tokens are allowed to be {@code null} or 
     * empty.
     * @return either the parsed value (right) or a parse error message (left).
     * @throws NullPointerException if the argument is {@code null}.
     */
    default Either<String, T> parse(Stream<String> tokens) {
        requireNonNull(tokens, "tokens");
        return parseNonNull(tokens);
    }
    
    /**
     * Convenience method, same as {@link #parse(Stream)}.
     * @param tokens the input tokens.
     * @return the parse result.
     */
    default Either<String, T> parse(String...tokens) {
        requireNonNull(tokens, "tokens");
        return parseNonNull(Stream.of(tokens));
    }
    
    /**
     * Combines this parser with a validator.
     * First applies this parser to turn tokens into a {@code T}-value,
     * then the given validator decides if the parsed value is legit. 
     * If this parser fails, then the process stops there and the parse error
     * is returned; otherwise the parsed value is given to the validator.
     * If validation fails, then the validation error is returned; otherwise
     * the parsed value is returned.
     * @param validator the validation to apply to the parsed value.
     * @return a parser that uses this object to do the parsing and then applies
     * the given validator to the parsed result.
     * @throws NullPointerException if the argument is {@code null}.
     */
    default ObjectParser<T> withValidation(Validator<String, T> validator) {
        requireNonNull(validator);
        return tokens -> parse(tokens).bind(validator::validate);
    }
    
}
/* NOTE. A poor man's interface for parsing.
 * Moderately useful for validation, but seriously hampered by the lack of 
 * composability. Use it if it fits your needs but be aware that there are 
 * way better options out there, most notably parser combinator libraries.
 *
 * To see why this approach to parsing is lame think of how you could combine
 * two parsers into a third, e.g. an integer parser with one that checks if
 * the integer is positive. Well, I needed to make a special case for that 
 * (withValidation method) even though, in principle, it's not really a very
 * a different concept: 
 * 
 *  + parsing = transforming some input into a result value or an error
 *  + validation = transforming an input into itself or an error
 *  
 * Surely these guys must be siblings? (In fact they belong to the same family
 * of functions; you can come up with a polymorphic function to represent the
 * whole family.)  
 * I'm kicking myself. Nuff said. 
 */

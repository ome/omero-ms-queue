package ome.smuggler.core.types;

import static java.util.Objects.requireNonNull;
import static util.string.Strings.isNullOrEmpty;
import static util.validation.ParserFactory.pairParser;
import static util.validation.ParserFactory.filePathUriParser;
import static util.validation.ParserFactory.positiveIntParser;
import static util.validation.ParserFactory.positiveLongParser;
import static util.validation.ParserFactory.stringParser;
import static util.validation.ParserFactory.uriParser;

import java.net.URI;
import java.time.Duration;
import java.util.function.Function;

import util.object.Either;
import util.types.Email;
import util.types.PositiveN;

/**
 * Methods to instantiate valid values of a certain type from their string
 * representation.
 */
public class ValueParserFactory {

    private static <T extends PositiveN>
    Either<String, T> parsePosInt(String value, Function<Long, T> mapper) {
        return positiveLongParser().parse(value).map(mapper);
    }
    
    /**
     * Creates a dataset ID from a positive integer.
     * @param value string representation of a positive integer.
     * @return either a (right) value or an error message (left) detailing why
     * the value could not be instantiated.
     */
    public static Either<String, DatasetId> datasetId(String value) {
        return parsePosInt(value, DatasetId::new);
    }
    
    /**
     * Creates a screen ID from a positive integer.
     * @param value string representation of a positive integer.
     * @return either a (right) value or an error message (left) detailing why
     * the value could not be instantiated.
     */
    public static Either<String, ScreenId> screenId(String value) {
        return parsePosInt(value, ScreenId::new);
    }
    
    /**
     * Creates an email from its string representation.
     * @param value string representation of an email address, e.g. {@code 
     * x@y.edu}.
     * @return either a (right) value or an error message (left) detailing why
     * the value could not be instantiated.
     */
    public static Either<String, Email> email(String value) {
        return stringParser()
              .withValidation(Email.validator())
              .parse(value)
              .map(Email::new);
    }
    
    /**
     * Creates a positive natural from its string representation.
     * @param value string representation of a positive integer.
     * @return either a (right) value or an error message (left) detailing why
     * the value could not be instantiated.
     */
    public static Either<String, PositiveN> positiveInt(String value) {
        return parsePosInt(value, PositiveN::of);
    }
    
    /**
     * Creates a text annotation from its string representation.
     * @param xs a pair of strings: a namespace followed by the annotation 
     * content.
     * @return either a (right) value or an error message (left) detailing why
     * the value could not be instantiated.
     */
    public static Either<String, TextAnnotation> textAnnotation(String...xs) {
        return pairParser(stringParser(), stringParser())
              .parse(xs == null ? new String[0] : xs)
              .map(TextAnnotation::new);
    }
    
    /**
     * Creates a URI from its string representation.
     * @param value string representation of a URI, e.g. {@code /some/file}.
     * @return either a (right) value or an error message (left) detailing why
     * the value could not be instantiated.
     */
    public static Either<String, URI> uri(String value) {
        return uriParser().parse(value);
    }

    /**
     * Creates a URI from the target field in the import request.
     * @param value the target field.
     * @return either a (right) value or an error message (left) detailing why
     * the value could not be instantiated.
     */
    public static Either<String, URI> targetUri(String value) {
        Either<String, URI> parsed = uriParser().parse(value);
        if (parsed.isLeft() || isNullOrEmpty(parsed.getRight().getScheme())) {
            parsed = filePathUriParser().parse(value);
        }
        return parsed;
    }

    /**
     * Creates a URI from host and port components: the returned URI will be of
     * the form {@code omero://host:port/}. 
     * @param host the host component of the URI.
     * @param port the port component of the URI.
     * @return either a (right) value or an error message (left) detailing why
     * the value could not be instantiated.
     */
    public static Either<String, URI> omeroUri(String host, String port) {
        return pairParser(stringParser(), positiveIntParser())
              .parse(host, port)
              .map(p -> String.format("omero://%s:%s/", p.fst(), p.snd()))
              .bind(ValueParserFactory::uri);
    }
    
    /**
     * Parses a string of length at least one.
     * @param value a string of length at least one.
     * @return either a (right) value or an error message (left) detailing why
     * the value could not be instantiated.
     */
    public static Either<String, String> string(String value) {
        return stringParser().parse(value);
    }

    /**
     * Parses the string representation of a positive integer as a {@link
     * Duration} in milliseconds.
     * @param value a string representation of a positive.
     * @return either a (right) value or an error message (left) detailing why
     * the value could not be instantiated.
     */
    public static Either<String, Duration> millis(String value) {
        return positiveInt(value).map(PositiveN::get).map(Duration::ofMillis);
    }
    
    /**
     * Prefixes the error message (i.e. any left value) with the specified 
     * label {@code p} so that if {@code e} is the original message, the new
     * message will be of the form {@code p:e}.
     * @param <T> any type.
     * @param errorMsgPrefix the string to prefix to the error message.
     * @param value the input to prefix in the case it's a left value.
     * @return the prefixed error message if the input either is a left value; 
     * otherwise the input's right value as is.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static <T> Either<String, T> label(String errorMsgPrefix, 
                                              Either<String, T> value) {
        requireNonNull(errorMsgPrefix, "errorMsgPrefix");
        requireNonNull(value, "value");
        
        return value.mapLeft(e -> String.format("%s: %s", errorMsgPrefix, e));
    }

}

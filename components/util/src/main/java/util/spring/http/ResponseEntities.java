package util.spring.http;

import static java.util.Objects.requireNonNull;
import static util.sequence.Arrayz.isNullOrZeroLength;

import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import util.object.Either;

/**
 * Helper methods for {@link ResponseEntity}'s.
 */
public class ResponseEntities {

    /**
     * Creates a new response with a 204 status and no body, as required for a
     * 204.
     * @return a new 204 response.
     */
    public static ResponseEntity<Void> _204() {
        //return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Creates a new response with the supplied body; if the body is a right 
     * value the response will be a 200, otherwise a 400.
     * @param <E> error response body type.
     * @param <R> response body type.
     * @param body either the content of a 200 (right value) or an error to
     * output in the body of a 400.
     * @return the response entity.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static <E, R> ResponseEntity<Object> okOr400(Either<E, R> body) {
        return okOrError(body, ResponseEntities::_400);
    }
    
    /**
     * Creates a new response with the supplied body; if the body is a right 
     * value the response will be a 200, otherwise a 400.
     * @param <E> error response body type.
     * @param <R> response body type.
     * @param errorOrResult the response body producer. The produced body can
     * either be the content of a 200 (right value) or an error to output in 
     * the body of a 400.
     * @return the response entity.
     * @throws NullPointerException if the supplier is {@code null} or the 
     * {@link Either} value produced by the supplier is {@code null}.
     */
    public static <E, R> ResponseEntity<Object> okOr400(
            Supplier<Either<E, R>> errorOrResult) {
        requireNonNull(errorOrResult, "errorOrResult");
        return okOr400(errorOrResult.get());
    }
    
    /**
     * Creates a new 400 response with an optional body.
     * If called with no arguments as in {@code _400()}, the response will have
     * an empty body; if a {@code body} argument is given, it will be written
     * to the response body using the configured Spring MVC message converters.
     * @param <T> response body type.
     * @param body optional response body.
     * @return a new 400 response.
     */
    @SafeVarargs
    public static <T> ResponseEntity<T> _400(T...body) {
        return newError(HttpStatus.BAD_REQUEST, body);
    }
    
    /**
     * Creates a new response with the supplied body; if the body is a right 
     * value the response will be a 200, otherwise a 404.
     * @param <E> error response body type.
     * @param <R> response body type.
     * @param body either the content of a 200 (right value) or an error to
     * output in the body of a 404.
     * @return the response entity.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static <E, R> ResponseEntity<Object> okOr404(Either<E, R> body) {
        return okOrError(body, ResponseEntities::_404);
    }
    
    /**
     * Creates a new response with the supplied body; if the body is a right 
     * value the response will be a 200, otherwise a 404.
     * @param <E> error response body type.
     * @param <R> response body type.
     * @param errorOrResult the response body producer. The produced body can
     * either be the content of a 200 (right value) or an error to output in 
     * the body of a 404.
     * @return the response entity.
     * @throws NullPointerException if the supplier is {@code null} or the 
     * {@link Either} value produced by the supplier is {@code null}.
     */
    public static <E, R> ResponseEntity<Object> okOr404(
            Supplier<Either<E, R>> errorOrResult) {
        requireNonNull(errorOrResult, "errorOrResult");
        return okOr404(errorOrResult.get());
    }
    
    /**
     * Creates a new 404 response with an optional body.
     * If called with no arguments as in {@code _404()}, the response will have
     * an empty body; if a {@code body} argument is given, it will be written
     * to the response body using the configured Spring MVC message converters.
     * @param <T> response body type.
     * @param body optional response body.
     * @return a new 404 response.
     */
    @SafeVarargs
    public static <T> ResponseEntity<T> _404(T...body) {
        return newError(HttpStatus.NOT_FOUND, body);
    }
    
    /**
     * Creates a new response with the supplied body; if the body is a right 
     * value the response will be a 200, otherwise a 406.
     * @param <E> error response body type.
     * @param <R> response body type.
     * @param body either the content of a 200 (right value) or an error to
     * output in the body of a 406.
     * @return the response entity.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static <E, R> ResponseEntity<Object> okOr406(Either<E, R> body) {
        return okOrError(body, ResponseEntities::_406);
    }
    
    /**
     * Creates a new response with the supplied body; if the body is a right 
     * value the response will be a 200, otherwise a 406.
     * @param <E> error response body type.
     * @param <R> response body type.
     * @param errorOrResult the response body producer. The produced body can
     * either be the content of a 200 (right value) or an error to output in 
     * the body of a 406.
     * @return the response entity.
     * @throws NullPointerException if the supplier is {@code null} or the 
     * {@link Either} value produced by the supplier is {@code null}.
     */
    public static <E, R> ResponseEntity<Object> okOr406(
            Supplier<Either<E, R>> errorOrResult) {
        requireNonNull(errorOrResult, "errorOrResult");
        return okOr406(errorOrResult.get());
    }
    
    /**
     * Creates a new 406 response with an optional body.
     * If called with no arguments as in {@code _406()}, the response will have
     * an empty body; if a {@code body} argument is given, it will be written
     * to the response body using the configured Spring MVC message converters.
     * @param <T> response body type.
     * @param body optional response body.
     * @return a new 406 response.
     */
    @SafeVarargs
    public static <T> ResponseEntity<T> _406(T...body) {
        return newError(HttpStatus.NOT_ACCEPTABLE, body);
    }
     
    private static <T> 
    ResponseEntity<T> newError(HttpStatus status, T[] maybeBody) {
        if (isNullOrZeroLength(maybeBody)) {
            return new ResponseEntity<>(status);
        }
        return new ResponseEntity<>(maybeBody[0], status);
    }
    /* NOTE.
     * HttpStatus is an enum which specifies both the code and reason phrase 
     * (they can be accessed with their respective enum getter methods) but
     * the reason phrase seems to be ignored by the framework when producing
     * a response from a response entity. If you need the reason phrase too,
     * annotate the method with, e.g.
     * 
     *     @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Not Found")
     */
    
    private static <E, R> ResponseEntity<Object> okOrError(
            Either<E, R> body,
            Function<E, ResponseEntity<Object>> errorGenerator) {
        requireNonNull(body, "body");

        return body.either(errorGenerator, ResponseEntity::ok);
    }
    
}

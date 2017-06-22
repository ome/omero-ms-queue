package util.lambda;

import static util.error.Exceptions.throwAsIfUnchecked;

import java.util.function.Consumer;

/**
 * Same as {@link Consumer} but can throw a checked exception.
 * This way you can use a consumer that throws as an argument to methods that 
 * would not type-check with an exception-throwing lambda argument.
 * Any thrown exception bubbles up as is without any wrapping as we use {@code 
 * throwAsIfUnchecked} to fool compiler and JVM.
 * @see 
 * <a href="http://programmers.stackexchange.com/questions/225931/workaround-for-java-checked-exceptions">
 * this...</a>
 * @see
 * <a href="http://stackoverflow.com/questions/18198176/java-8-lambda-function-that-throws-exception">
 * ...and that.</a>
 */
@FunctionalInterface
public interface ConsumerE<T> extends Consumer<T> {

    @Override
    default void accept(T t) {
        try {
            acceptE(t);
        } catch (Exception e) {
            throwAsIfUnchecked(e);
        }
    }

    void acceptE(T t) throws Exception;
    
}

package util.lambda;

import static util.error.Exceptions.throwAsIfUnchecked;

import java.util.function.BiConsumer;

/**
 * Same as {@link BiConsumer} but can throw a checked exception.
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
public interface BiConsumerE<T, U> extends BiConsumer<T, U> {

    @Override
    default void accept(T t, U u) {
        try {
            acceptE(t, u);
        } catch (Exception e) {
            throwAsIfUnchecked(e);
        }
    }

    void acceptE(T t, U u) throws Exception;

}

package util.lambda;

import static util.error.Exceptions.throwAsIfUnchecked;

import java.util.function.Supplier;

/**
 * Same as {@link Supplier} but can throw a checked exception.
 * This way you can use a supplier that throws as an argument to methods that 
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
public interface SupplierE<T> extends Supplier<T> {

    @Override
    default T get() {
        try {
            return getE();
        } catch (Exception e) {
            throwAsIfUnchecked(e);
        }
        return null;  // never reached, but keeps compiler happy
    }

    T getE() throws Exception;
    
}

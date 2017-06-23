package util.error;

import static java.util.Objects.requireNonNull;
import static util.sequence.Arrayz.hasNulls;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import util.lambda.*;
import util.object.Either;

/**
 * Helper methods to work with exceptions.
 */
public class Exceptions {

    /**
     * Masks the given exception as a runtime (unchecked) exception and throws
     * it as such, fooling compiler and JVM runtime. Use with extreme care! 
     * @param t the exception to recast to {@link RuntimeException}.
     * @return nothing as {@code t} will be thrown.
     * @see
     * <a href="http://www.philandstuff.com/2012/04/28/sneakily-throwing-checked-exceptions.html">
     * How does it work?</a>
     * @see
     * <a href="http://programmers.stackexchange.com/questions/225931/workaround-for-java-checked-exceptions">
     * Advantages, disadvantages, and limitations.
     * </a>
     */
    public static RuntimeException throwAsIfUnchecked(Throwable t) {
        requireNonNull(t, "t");
                
        Exceptions.throwAs(t);
        return null;
    }
    
    @SuppressWarnings("unchecked")
    private static 
    <T extends Throwable> void throwAs(Throwable t) throws T {
        throw (T) t;
    }

    /* How does it work?
     * 1. Exceptions.<RuntimeException> throwAs(t)
     *    selects this method:
     *    void throwAs(Throwable t) throws RuntimeException
     * 2. b/c of (1), throwAs casts t to RuntimeException
     * 3. complier assumes uncheck throws RuntimeException so doesn't require
     *    throws clause in uncheck signature
     * 4. compiler defers cast check to runtime: if t doesn't turn out to be
     *    an instance of T, runtime should throw ClassCastException
     * 5. compiler erases selected type of T, RuntimeException
     * 6. JVM runtime has no means to tell t's type so it just throws it!
     * 
     * ...another episode of the long-running Sun-then-Oracle-made sitcom:
     * "Java's Type System".
     * According to the interwebs this guy should be given brownie points
     * for putting the hack together:
     * - http://www.mail-archive.com/javaposse@googlegroups.com/msg05984.html 
     */
    
    /**
     * Convenience up-cast so that an exception-throwing lambda can be used
     * in place of a normal one.
     * @param <T> any type.
     * @param mayThrowChecked value to up-cast.
     * @return the up-cast input.
     */
    public static <T> Consumer<T> unchecked(ConsumerE<T> mayThrowChecked) {
        return mayThrowChecked;
    }

    /**
     * Convenience up-cast so that an exception-throwing lambda can be used
     * in place of a normal one.
     * @param <T> any type.
     * @param <U> any type.
     * @param mayThrowChecked value to up-cast.
     * @return the up-cast input.
     */
    public static <T, U> BiConsumer<T, U> unchecked(BiConsumerE<T, U> mayThrowChecked) {
        return mayThrowChecked;
    }

    /**
     * Convenience up-cast so that an exception-throwing lambda can be used
     * in place of a normal one.
     * @param <T> any type.
     * @param mayThrowChecked value to up-cast.
     * @return the up-cast input.
     */
    public static <T> Supplier<T> unchecked(SupplierE<T> mayThrowChecked) {
        return mayThrowChecked;
    }
    
    /**
     * Convenience up-cast so that an exception-throwing lambda can be used
     * in place of a normal one.
     * @param <T> any type.
     * @param <R> any type.
     * @param mayThrowChecked value to up-cast.
     * @return the up-cast input.
     */
    public static <T, R> Function<T, R> unchecked(FunctionE<T, R> mayThrowChecked) {
        return mayThrowChecked;
    }

    /**
     * Converts an action into a {@link Runnable} by masking any raised
     * exception as a runtime exception and rethrowing it as is.
     * @param target the action to convert.
     * @return an adapter that makes the target action work as a
     * {@link Runnable}.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static Runnable unchecked(ActionE target) {
        requireNonNull(target, "target");
        return () -> runUnchecked(target);
    }

    /**
     * Runs the given action, re-throwing any exception without wrapping. 
     * @param mayThrowChecked the action to run.
     * @see #throwAsIfUnchecked(Throwable)
     */
    public static void runUnchecked(ActionE mayThrowChecked) {
        try {
            mayThrowChecked.run();
        } catch (Exception e) {
            throwAsIfUnchecked(e);
        }
    }

    /**
     * Runs each action and catches any thrown exception.
     * Exceptions are collected in the returned array {@code r} in the same
     * order in which actions {@code xs} are passed in to this method, i.e.
     * {@code r[k]} corresponds to {@code xs[k]} for {@code k = 0, ...,
     * xs.length}. If an action doesn't throw, then the corresponding element
     * in the returned array will be the empty optional.
     * @param xs the actions to run.
     * @return any exception occurred in the same order in which the actions
     * arguments were specified.
     * @throws NullPointerException if the actions array or any of its elements
     * is {@code null}.
     */
    @SuppressWarnings("unchecked")
    public static Optional<Throwable>[] runAndCatch(ActionE...xs) {
        if (xs == null || hasNulls(xs)) {
            throw new NullPointerException("null action(s)");
        }
        return Stream.of(xs)
                     .map(x -> {
                         try {
                             x.run();
                             return Optional.empty();
                         } catch (Throwable t) {
                             return Optional.of(t);
                         }
                     })
                     .toArray(Optional[]::new);
    }

    /**
     * Runs each action, swallowing any thrown {@link Throwable}.
     * @param xs the actions to run.
     * @throws NullPointerException if the actions array or any of its elements
     * is {@code null}.
     */
    public static void runAndSwallow(ActionE...xs) {
        runAndCatch(xs);
    }
    
    /**
     * Either gets the right value or throws the left exception.
     * @param <T> any type.
     * @param errorOrValue an error or a value.
     * @return the value if the argument is a right; otherwise the given
     * exception will be thrown.
     */
    public static <T> T getOrThrow(Either<? extends Exception, T> errorOrValue) {
        requireNonNull(errorOrValue, "errorOrValue");
        if (errorOrValue.isLeft()) {
            throwAsIfUnchecked(errorOrValue.getLeft());
        }
        return errorOrValue.getRight();
    }
    
}

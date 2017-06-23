package util.runtime;

import static java.util.Objects.requireNonNull;
import static util.object.Either.right;

import java.util.Optional;
import java.util.stream.Stream;

import util.object.Either;

/**
 * Base class to enforce the set/build protocol required by the {@link 
 * ProgramArgument} interface.
 */
public class BaseProgramArgument<T> implements ProgramArgument<T> {

    private Optional<T> maybeArg;
    
    /**
     * Creates a new instance.
     */
    public BaseProgramArgument() {
        maybeArg = Optional.empty();
    }
    
    /**
     * Creates a new instance to hold the specified argument.
     * @param arg the argument.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public BaseProgramArgument(T arg) {
        this();
        set(arg);
    }

    @Override
    public ProgramArgument<T> set(T arg) {
        requireNonNull(arg);
        maybeArg = validate(arg).either(this::throwIfNotValid, Optional::of);
        return this;
    }
    
    private Optional<T> throwIfNotValid(String errorMessage) {
        throw new IllegalArgumentException(errorMessage);
    }
    
    /**
     * Override this method to do additional validation of the argument to set.
     * @param argToSet the value to validate.
     * @return either an error message if validation fails or the argument 
     * itself if validation succeeds.
     */
    protected Either<String, T> validate(T argToSet) {
        return right(argToSet);
    }

    @Override
    public Stream<String> tokens() {
        if (!maybeArg.isPresent()) {
            throw new IllegalStateException("argument not set yet");
        }
        return tokenize(maybeArg.get());
    }
    
    /**
     * Override this method to convert the argument into token components if
     * the default of calling the argument's {@code toString} method is not
     * suitable.
     * @param arg the argument to convert to tokens.
     * @return the tokenized representation of the argument as required by the
     * {@link #tokens() tokens} method.
     */
    protected Stream<String> tokenize(T arg) {
        return Stream.of(arg.toString());
    }

}

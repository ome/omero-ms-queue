package util.runtime;

/**
 * A typed program argument.
 */
public interface ProgramArgument<T> extends CommandBuilder {
    
    /**
     * Sets this argument's payload.
     * @param arg the value to set.
     * @return itself for use in a fluent API style.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws IllegalArgumentException if the argument is not suitable.
     */
    ProgramArgument<T> set(T arg);

}

package util.object;

import java.util.function.Supplier;

/**
 * A {@link Supplier} to wrap a value in such a way that the wrapper behaves 
 * just like the wrapped value in terms of {@link #equals(Object) equality}, 
 * {@link #hashCode() hashing}, and {@link #toString() string conversion}.
 */
public class Wrapper<T> extends AbstractWrapper<T> {

    protected final T wrappedValue;
    
    /**
     * Creates a new wrapper for the given value.
     * @param wrappedValue the value to wrap, may be {@code null}.
     */
    public Wrapper(T wrappedValue) {
        this.wrappedValue = wrappedValue;
    }
    
    /**
     * @return the wrapped value.
     */
    @Override
    public T get() {
        return wrappedValue;
    }
    
}

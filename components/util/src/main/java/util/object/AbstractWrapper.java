package util.object;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * A {@link Supplier} to wrap a value in such a way that the wrapper behaves 
 * just like the wrapped value in terms of {@link #equals(Object) equality}, 
 * {@link #hashCode() hashing}, and {@link #toString() string conversion}.
 */
public abstract class AbstractWrapper<T> implements Supplier<T> {

    /**
     * Implements the equivalence obtained by identifying wrappers with wrapped
     * values.
     * More precisely, call {@code U} the set obtained by the union of all 
     * {@code T} values ({@code null} included) with their corresponding 
     * wrappers {@code W = { AbstractWrapper(t) | t in T }} and consider the
     * function {@code f : U → T} defined by {@code f t = t} if {@code t in T}
     * or {@code f w = w.get()} if {@code w in W}. The equivalence enforced
     * by this method is the equivalence kernel of {@code f}.
     */
    @Override 
    public boolean equals(Object other) {
        if (this == other) return true;
        
        T wrappedValue = get();
        if (other instanceof AbstractWrapper) {  // NB (null instanceOf ..) == false
            AbstractWrapper<?> otherWrapper = (AbstractWrapper<?>) other;
            return Objects.equals(wrappedValue, otherWrapper.get());
        }
        if (wrappedValue != null) {
            return wrappedValue.equals(other);
        }
        return other == null;
    }
    
    /**
     * Implemented by delegating to the wrapped value except when {@code null},
     * in which case {@code 0} is returned.
     */
    @Override 
    public int hashCode() {
        T wrappedValue = get();
        return wrappedValue == null ? 0 : wrappedValue.hashCode();
    }
    
    /**
     * Implemented by delegating to the wrapped value except when {@code null},
     * in which case the string "null" is returned.
     */
    @Override
    public String toString() {
        T wrappedValue = get();
        return String.valueOf(wrappedValue);  // caters for null
    }

}

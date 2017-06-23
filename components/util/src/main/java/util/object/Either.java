package util.object;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

/**
 * Holds either a value of type {@code L}, referred to as the "left value", or a 
 * value of type {@code R}, referred to as the "right value".
 * Often used in situations where the outcome of a computation can either be 
 * some value {@code R} (the right, correct value) or some other value {@code L}
 * indicating an error condition. 
 * <p>
 * NOTE. Most of this class is based on the core Haskell API for the Either 
 * data type.
 * </p>
 */
public class Either<L, R> {

    /**
     * Creates a new holder for a left value.
     * @param <L> left value type.
     * @param <R> right value type.
     * @param leftValue the value to hold, may be {@code null}.
     * @return a left value holder.
     */
    public static <L, R> Either<L, R> left(L leftValue) {
        return new Either<>(leftValue, true, null);
    }
    
    /**
     * Creates a new holder for a right value.
     * @param <L> left value type.
     * @param <R> right value type.
     * @param rightValue the value to hold, may be {@code null}.
     * @return a right value holder.
     */
    public static <L, R> Either<L, R> right(R rightValue) {
        return new Either<>(null, false, rightValue);
    }
    
    private final L maybeLeft;
    private final R maybeRight;
    private final boolean isLeftValue;
    
    private Either(L leftValue, boolean isLeft, R rightValue) {
        maybeLeft = leftValue;
        maybeRight = rightValue;
        this.isLeftValue = isLeft;
    }
    
    /**
     * Is this a left value holder?
     * @return {@code true} for yes, {@code false} for no.
     */
    public boolean isLeft() {
        return isLeftValue;
    }
    
    /**
     * Is this a right value holder?
     * @return {@code true} for yes, {@code false} for no.
     */
    public boolean isRight() {
        return !isLeftValue;
    }
    
    /**
     * Returns the left value if this is a {@link #isLeft() left} value holder;
     * {@code null} will be merciless returned if this is instead a {@link 
     * #isRight() right} value holder;
     * @return the left value.
     */
    public L getLeft() {
        return maybeLeft;
    }
    
    /**
     * Returns the right value if this is a {@link  #isRight() right} value 
     * holder; {@code null} will be merciless returned if this is instead a  
     * {@link #isLeft() left} value holder;
     * @return the right value.
     */
    public R getRight() {
        return maybeRight;
    }
    
    /**
     * Applies the given transformation to the right value if this is a {@link  
     * #isRight() right} value holder to produce a new {@link #isRight() right} 
     * value holder to store the result of the transformation.
     * Does nothing if this is a {@link #isLeft() left} value holder, just 
     * returning a new left value holder containing this left value; this way a 
     * left value is propagated as is through map calls.
     * @param <T> the mapped type.
     * @param f the transformation to apply to the right value.
     * @return a new right value holder with the mapped value if this is a
     * right value holder; otherwise a new left value holder with this object's
     * left value.
     * @throws NullPointerException if this is a right value holder and the
     * argument is {@code null}.
     */
    public <T> Either<L, T> map(Function<R, T> f) {
        if (isRight()) {
            requireNonNull(f, "f");
            
            T mappedValue = f.apply(maybeRight);
            return right(mappedValue);
        }
        return left(maybeLeft);
    }
    
    /**
     * Same as {@link #map(Function) map} but on left values.
     * @param <T> the mapped type.
     * @param f the left value mapper.
     * @return a new left value holder with the mapped value if this is a
     * left value holder; otherwise a new right value holder with this object's
     * right value.
     */
    public <T> Either<T, R> mapLeft(Function<L, T> f) {
        if (isLeft()) {
            requireNonNull(f, "f");
            
            T mappedValue = f.apply(maybeLeft);
            return left(mappedValue);
        }
        return right(maybeRight);
    }
    
    /**
     * Applies the given transformation to the to the right value if this is a 
     * {@link #isRight() right} value holder to produce a new value holder.
     * Does nothing if this is a {@link #isLeft() left} value holder, just 
     * returning a new left value holder containing this left value; this way a 
     * left value is propagated as is through map calls.
     * @param <T> the mapped type.
     * @param f the new value holder producer to apply to the right value.
     * @return the result of applying {@code f} to the right value if this
     * is a right value holder; otherwise a new left value holder with this 
     * object's left value.
     * @throws NullPointerException if this is a right value holder and the
     * argument is {@code null}.
     */
    public <T> Either<L, T> bind(Function<R, Either<L, T>> f) {
        if (isRight()) {
            requireNonNull(f, "f");
            
            return f.apply(maybeRight);
        }
        return left(maybeLeft);
    }
    
    /**
     * Applies {@code left} to the left value if this is a {@link #isLeft() 
     * left} value holder; otherwise applies {@code right} to the right value.
     * @param <T> the mapped type.
     * @param left the transformation to apply to the left value.
     * @param right the transformation to apply to the right value.
     * @return the result of applying either transformation.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public <T> T either(Function<L, T> left, Function<R, T> right) {
        requireNonNull(left, "left");
        requireNonNull(right, "right");
        
        if (isLeftValue) {
            return left.apply(maybeLeft);
        }
        else {
            return right.apply(maybeRight);
        }
    }
    
}

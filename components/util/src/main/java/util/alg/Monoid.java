package util.alg;

/**
 * Classes implementing this interface state that {@code T}-values are the 
 * objects of a <a href="https://en.wikipedia.org/wiki/Monoid">monoid</a>.
 * Consequently an implementing class provides both the binary operation and
 * identity that make the {@code T}-values into a monoid.
 * Methods in this interface are named with multiplicative notation in mind:
 * the binary operation is the "product" and the identity is the "unit".
 */
public interface Monoid<T> {

    /**
     * This monoid's identity.
     * Even though any two calls to this method may not return the same object, 
     * the returned objects {@code u} and {@code u'} have to be equal according 
     * to the {@link Object#equals(Object) equals} method. 
     * @return this monoid's identity.
     */
    T unit();
    
    /**
     * The monoid's product, i.e. its associative and unit-bearing binary 
     * operation.
     * @param x value to multiply.
     * @param y value to multiply.
     * @return the product of {@code x} and {@code y}.
     * @throws NullPointerException if any argument is {@code null}.
     */
    T multiply(T x, T y);
    
}

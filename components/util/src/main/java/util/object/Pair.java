package util.object;

import java.util.Objects;

/**
 * A pair {@code (x,y)} in {@code A×B}.
 */
public class Pair<A, B> {

    /**
     * Syntactic sugar to use in place of the constructor.
     * @param <A> first element type.
     * @param <B> second element type.
     * @param x the first element.
     * @param y the second element.
     * @return a new pair.
     */
    public static <A, B> Pair<A, B> pair(A x, B y) {
        return new Pair<>(x, y);
    }

    
    private final A fst;
    private final B snd;

    /**
     * Creates a new pair.
     * @param x the first element.
     * @param y the second element.
     */
    public Pair(A x, B y) {
        fst = x;
        snd = y;
    }

    /**
     * @return the first element of the pair.
     */
    public A fst() {
        return fst;
    }

    /**
     * @return the second element of the pair.
     */
    public B snd() {
        return snd;
    }

    @Override 
    public boolean equals(Object x) {
        if (this == x) return true;               // avoid unnecessary work
        if (x instanceof Pair) {                  // false if x == null or type differs
            Pair<?, ?> other = (Pair<?, ?>) x;    // best we can do, courtesy of type erasure
            return Objects.equals(fst, other.fst) // crossing fingers their equals methods check
                && Objects.equals(snd, other.snd);// the type is the same
         }
         return false;
    }
    /* NOTE. See also:
     * - http://www.angelikalanger.com/GenericsFAQ/FAQSections/ProgrammingIdioms.html#FAQ501
     */
    
    @Override 
    public int hashCode() {
        return Objects.hash(fst, snd);
    }
    
    @Override
    public String toString() {
        return String.format("(%s, %s)", fst, snd);
    }
    
}

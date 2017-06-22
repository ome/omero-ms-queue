package util.object;

/**
 * A way to identify an object in a given context.
 */
public interface Identifiable {

    /**
     * Produces the unique ID that identifies this object in some given
     * context.
     * This method is defaulted to return the string {@code fqn@ref}, where
     * {@code fqn} is the fully qualified class name of this object and 
     * {@code ref} is the HEX representation of this object's native hash code
     * (i.e. what is returned by {@link System#identityHashCode(Object) 
     * identityHashCode}) which is usually the memory address of the object.
     * This is the same of what {@code toString} returns if it has not been
     * overwritten and {@code hashCode} has not been overwritten either.
     * @return this object's ID.
     */
    default String id() {
        return String.format("%s@%h", 
                             this.getClass().getName(),
                             System.identityHashCode(this));
    }

    /**
     * Hashes this object's {@link #id() ID} so that the returned code is in
     * the range {@code [0, size - 1]} (endpoints are included).
     * This method is defaulted to use a hash function that assumes the ID's
     * are bounded-length strings occurring with uniform probability.
     * This would likely be the case for UUID's, but in general you should
     * provide a suitable implementation depending on the type of ID you're
     * using.
     * @param size the number of buckets for the hash function, i.e. the size
     *             of the codomain.
     * @return the hashed ID.
     */
    default int hashedId(int size) {
        int m = (size < 0 ? -size : size);
        return m == 0 ? 0
                      : ((id().hashCode() % m) + m) % m;  // (*)
    }
    /* (*) we need this as hashCode may return negative values and so does %
     * when the first argument is negative:
     *      -3 % 3 =  0	  (    in maths: -3 mod 3 = 0)
     *      -2 % 3 = -2	  (but in maths: -2 mod 3 = 1)
     *      -1 % 3 = -1	  (but in maths: -1 mod 3 = 2)
     * i.e. in maths we take the remainder of the Euclidean division, whereas
     * % merely returns a number that is congruent to it.
     */
}

package util.types;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

/**
 * A plan for carrying out a task on an item {@code T} at a specified future 
 * time.
 */
public class Schedule<T> {

    private final FutureTimepoint when;
    private final T what;
    
    /**
     * Creates a new instance.
     * @param when when to execute the schedule.
     * @param what what to schedule.
     */
    public Schedule(FutureTimepoint when, T what) {
        requireNonNull(when, "when");
        requireNonNull(what, "when");
        
        this.when = when;
        this.what = what;
    }
    
    /**
     * @return when to execute the schedule.
     */
    public FutureTimepoint when() {
        return when;
    }

    /**
     * @return what to schedule.
     */
    public T what() {
        return what;
    }
    
    @Override
    public boolean equals(Object x) {
        if (x == this) {                   // avoid unnecessary work
            return true;
        }
        if (x instanceof Schedule) {       // false if x == null or type differs
            Schedule<?> other = (Schedule<?>) x;          // best we can do, courtesy of type erasure
            return Objects.equals(this.when, other.when)  // crossing fingers their equals methods check
                && Objects.equals(this.what, other.what); // the type is the same
        }
        return false;
    }
    /* NOTE. See also:
     * - http://www.angelikalanger.com/GenericsFAQ/FAQSections/ProgrammingIdioms.html#FAQ501
     */
    
    @Override
    public int hashCode() {
        return Objects.hash(when, what);
    }
    
    @Override
    public String toString() {
        return String.format("(%s, %s)", when, what);
    }

}

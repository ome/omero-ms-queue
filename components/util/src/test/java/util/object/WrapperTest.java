package util.object;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import static util.sequence.Arrayz.array;

import java.util.Objects;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class WrapperTest {

    @DataPoints
    public static final String[] values = array(null, "", "a", "ab");
    
    @SuppressWarnings("unchecked")
    @DataPoints
    public static Wrapper<String>[] wrappers = Stream.of(values)
                                                     .map(Wrapper::new)
                                                     .skip(1)  // get rid of W(null)
                                                     .toArray(Wrapper[]::new);
    
    @Theory
    public void equalityOfWrappersAgreesWithEqualityOfWrappedValues(String x, String y) {
        Wrapper<String> wx = new Wrapper<>(x);
        Wrapper<String> wy = new Wrapper<>(y);
        assertThat(wx.equals(wy), is(Objects.equals(x, y)));
    }
    
    @Theory
    public void equalsDelegatesToWrappedValue(Wrapper<String> w, String x) {
        assumeTrue(w != null);  // (*)
        
        String wrappedValue = w.get();
        assertThat(w.equals(x), is(wrappedValue.equals(x)));
    }
    /* (*) there's no null in wrappers, but JUnit kindly gives us one so that
     * our actual supply of wrappers = [null, w(""), w("a"), w("ab")]. WTH?!
     */
    
    @Theory
    public void equalsComparesNullsWhenWrappedValueIsNull(String x) {
        Wrapper<String> w = new Wrapper<>(null);
        assertThat(w.equals(x), is(x == null));
    }
    
    @Theory
    public void hashCodeDelegatesToWrappedValue(Wrapper<String> w) {
        assumeTrue(w != null);  // (*)
        
        String wrappedValue = w.get();
        assertThat(w.hashCode(), is(wrappedValue.hashCode()));
    }
    /* (*) there's no null in wrappers, but JUnit kindly gives us one so that
     * our actual supply of wrappers = [null, w(""), w("a"), w("ab")]. WTH?!
     */
    
    @Test
    public void hashCodeIsZeroWhenWrappedValueIsNull() {
        Wrapper<String> w = new Wrapper<>(null);
        assertThat(w.hashCode(), is(0));
    }
    
    @Theory
    public void toStringDelegatesToWrappedValue(Wrapper<String> w) {
        assumeTrue(w != null);  // (*)
        
        String wrappedValue = w.get();
        assertThat(w.toString(), is(wrappedValue));
    }
    /* (*) there's no null in wrappers, but JUnit kindly gives us one so that
     * our actual supply of wrappers = [null, w(""), w("a"), w("ab")]. WTH?!
     */
    
    @Test
    public void toStringIsLiteralNullWhenWrappedValueIsNull() {
        Wrapper<String> w = new Wrapper<>(null);
        assertThat(w.toString(), is("null"));
    }
    
    @Test
    public void returnNullWhenWrappedValueIsNull() {
        Wrapper<String> w = new Wrapper<>(null);
        assertNull(w.get());
    }
    
    @Test
    public void returnWrappedValue() {
        String value = "";
        Wrapper<String> w = new Wrapper<>(value);
        assertTrue(w.get() == value);
    }
    
}

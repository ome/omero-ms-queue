package util.config.props;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.config.props.JPropKey.key;
import static util.config.props.JPropAccessorFactory.makeBool;
import static util.config.props.JPropAccessorFactory.makeString;
import static util.config.props.JPropAccessorFactory.makeURI;
import static util.sequence.Arrayz.array;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class JPropsTest {

    private static final JPropAccessor<Boolean> boolProp = makeBool(key("p1"));
    private static final JPropAccessor<URI> uriProp = makeURI(key("p2"));
    
    private static JProps emptyProps() {
        return new JProps(new Properties());
    }
    
    private static <T> String assertPropWasSet(
            JProps props, JPropAccessor<T> prop, T value) {
        Optional<T> maybeValue = props.get(prop);
        
        assertNotNull(maybeValue);
        assertTrue(maybeValue.isPresent());
        assertThat(maybeValue.get(), is(value));
        
        String k = prop.getKey().get();
        assertTrue(props.getProps().containsKey(k));
        
        return k;
    }
    
    private static <T> void assertWriteThenReadBackThenRemove(
            JProps props, JPropAccessor<T> prop, T value) {
        props.set(prop, value);
        
        String key = assertPropWasSet(props, prop, value);
        
        props.remove(prop);
        assertFalse(props.getProps().containsKey(key));
    }
    
    @DataPoints
    public static Boolean[] bools = array(true, false);
    
    @DataPoints
    public static URI[] uris = Stream.of("urn:1", "urn:2")
                                     .map(URI::create)
                                     .toArray(URI[]::new);
    
    private JProps props;
    
    @Before
    public void setup() {
        props = emptyProps();
    }
    
    @Theory
    public void canWriteThenReadBackThenRemove(Boolean b, URI u) {
        assertWriteThenReadBackThenRemove(props, boolProp, b);
        assertWriteThenReadBackThenRemove(props, uriProp, u);
    }
    
    @Test
    public void writesEmptyButNeverReadsItBack() {
        props.setEmpty(boolProp);
        
        String key = boolProp.getKey().get();
        String rawValue = props.getProps().getProperty(key);
        Optional<Boolean> lookupValue = props.get(boolProp);
        
        assertThat(rawValue, is(""));
        assertFalse(lookupValue.isPresent());
        assertFalse(Boolean.valueOf(""));   // (*)
    }
    /* (*) Boolean.valueOf("") == false, which is why we know JProps must have
     * handled explicitly the raw value of "", otherwise the look up value 
     * would have been Optional.of(false) and lookupValue.isPresent() == true. 
     */
    
    @Test
    public void returnEmptyOptionalIfPropIsNotInDb() {
        Optional<Boolean> lookupValue = props.get(boolProp);
        assertNotNull(lookupValue);
        assertFalse(lookupValue.isPresent());
    }
    
    @Test(expected = URISyntaxException.class)
    public void throwRuntimeExceptionIfFailsToConvertToObject() {
        props.getProps().setProperty(uriProp.getKey().get(), "   ");
        props.get(uriProp);
    }
    
    @Test
    public void setWithConsumer() {
        props.set(boolProp.with(true));
        assertPropWasSet(props, boolProp, true);
    }
    
    @Test
    public void setAllDoesNothingIfEmptyStream() {
        props.setAll(Stream.of(), true);
        assertTrue(props.getProps().isEmpty());
    }
    
    @Test
    public void setAllWritesSameValueForAllListedProps() {
        JPropAccessor<String> s1 = makeString(key("s1"));
        JPropAccessor<String> s2 = makeString(key("s2"));                      
        
        String value = "same";
        props.setAll(Stream.of(s1, s2), value);
        
        assertThat(props.get(s1).get(), is(value));
        assertThat(props.get(s2).get(), is(value));
    }
    
    @Test
    public void removeAllDoesNothingIfEmptyStream() {
        props.set(boolProp, true);
        props.removeAll(Stream.of());
        assertFalse(props.getProps().isEmpty());
    }
    
    @Test
    public void removeAllOnlyDeletesListedProps() {
        JPropAccessor<String> s1 = makeString(key("s1"));
        JPropAccessor<String> s2 = makeString(key("s2"));                      
        
        props.setAll(Stream.of(s1, s2), "same");
        props.set(boolProp, true);
        props.removeAll(Stream.of(s1, s2));
        
        assertFalse(props.get(s1).isPresent());
        assertFalse(props.get(s2).isPresent());
        assertTrue(props.get(boolProp).isPresent());
    }
    
    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfNullArg() {
        new JProps(null);
    }
    
    @Test(expected = NullPointerException.class)
    public void getThrowsIfNullArg() {
        props.get(null);
    }
    
    @Test(expected = NullPointerException.class)
    public void setThrowsIfNullFirstArg() {
        props.set(null, "");
    }
    
    @Test(expected = NullPointerException.class)
    public void setThrowsIfNullSecondArg() {
        props.set(boolProp, null);
    }
    
    @Test(expected = NullPointerException.class)
    public void setWithConsumerThrowsIfNullArg() {
        props.set(null);
    }
    
    @Test(expected = NullPointerException.class)
    public void setAllThrowsIfNullFirstArg() {
        props.setAll(null, "");
    }
    
    @Test(expected = NullPointerException.class)
    public void setAllThrowsIfNullSecondArg() {
        props.setAll(Stream.of(boolProp), null);
    }
    
    @Test(expected = NullPointerException.class)
    public void setAllThrowsIfNullProps() {
        props.setAll(Stream.of(boolProp, null), true);
    }
    
    @Test(expected = NullPointerException.class)
    public void setEmptyThrowsIfNullArg() {
        props.setEmpty(null);
    }
    
    @Test(expected = NullPointerException.class)
    public void removeThrowsIfNullArg() {
        props.remove(null);
    }
    
    @Test(expected = NullPointerException.class)
    public void removeAllThrowsIfNullArg() {
        props.removeAll(null);
    }
    
    @Test(expected = NullPointerException.class)
    public void removeAllThrowsIfNullProps() {
        props.removeAll(Stream.of(boolProp, null));
    }
    
}

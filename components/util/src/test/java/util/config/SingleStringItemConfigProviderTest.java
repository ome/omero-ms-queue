package util.config;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.sequence.Arrayz.array;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class SingleStringItemConfigProviderTest {

    @DataPoints
    public static String[] configValues = array(" ", " 1", "abc ");
    
    @Theory
    public void returnStringAsIsIfSupplierReturnsStringOfLenAtLeastOne(
            String configValue) {
        SingleStringItemConfigProvider target = 
                new SingleStringItemConfigProvider(() -> configValue);
        
        String[] streamedValue = target.defaultReadConfig()
                                       .toArray(String[]::new);
        
        assertNotNull(streamedValue);
        assertThat(streamedValue.length, is(1));
        assertThat(streamedValue[0], is(configValue));
        
        Optional<String> maybeValue = target.get();
        
        assertNotNull(maybeValue);
        assertTrue(maybeValue.isPresent());
        assertThat(maybeValue.get(), is(configValue));
    }
    
    private static void assertEmpty(SingleStringItemConfigProvider target) {
        Stream<String> streamedValue = target.defaultReadConfig();
        
        assertNotNull(streamedValue);
        assertThat(streamedValue.count(), is(0L));
        
        Optional<String> maybeValue = target.get();
        
        assertNotNull(maybeValue);
        assertFalse(maybeValue.isPresent());
    }
    
    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullArg() {
        new SingleStringItemConfigProvider(null);
    }
    
    @Test
    public void returnEmptyIfSupplierReturnsNull() {
        SingleStringItemConfigProvider target = 
                new SingleStringItemConfigProvider(() -> null);
        assertEmpty(target);
    }
    
    @Test
    public void returnEmptyIfSupplierReturnsEmptyString() {
        SingleStringItemConfigProvider target = 
                new SingleStringItemConfigProvider(() -> "");
        assertEmpty(target);
    }
    
}

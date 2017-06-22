package util.runtime;

import static org.junit.Assert.*;
import static util.sequence.Arrayz.array;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class BaseProgramArgumentTest {
    
    @DataPoints
    public static String[] values = array("", "x", " x y");
    
    @Theory
    public void stringSetIsReturnedAsIsByTokens(String arg) {
        target.set(arg);
        String[] actual = target.tokens().toArray(String[]::new);
        
        assertArrayEquals(array(arg), actual);
    }
    
    private BaseProgramArgument<String> target;
    
    @Before
    public void setup() {
        target = new BaseProgramArgument<>();
    }
    
    @Test (expected = NullPointerException.class)
    public void setThrowsIfNullArg() {
        target.set(null);
    }
    
    @Test (expected = IllegalStateException.class)
    public void tokensThrowsIfArgNotSetYet() {
        target.tokens();
    }
    
}

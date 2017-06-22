package util.runtime;

import static org.junit.Assert.*;
import static util.sequence.Arrayz.array;
import static util.sequence.Arrayz.asList;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import util.sequence.Arrayz;

@RunWith(Theories.class)
public class ListProgramArgumentTest {

    @DataPoints
    public static final Integer[][] lists = new Integer[][] {
        array(), array(1), array(1, 2), array(1, 2, 3)
    };
    
    @Theory
    public void listElementsTokenizedInListOrder(Integer[] arg) {
        String[] actual = new ListProgramArgument<Integer>()
                         .set(asList(arg))
                         .tokens()
                         .toArray(String[]::new);
        
        String[] expected = Arrayz.op(String[]::new)
                                  .map((ix, value) -> value.toString(), arg);
        
        assertArrayEquals(expected, actual);
    }
    
    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullArg() {
        new ListProgramArgument<>(null);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void ctorThrowsIfListHasNulls() {
        new ListProgramArgument<>(asList("", null, ""));
    }
    
    @Test (expected = NullPointerException.class)
    public void setThrowsIfNullArg() {
        new ListProgramArgument<>().set(null);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void setThrowsIfListHasNulls() {
        new ListProgramArgument<>().set(asList("", null, ""));
    }
}

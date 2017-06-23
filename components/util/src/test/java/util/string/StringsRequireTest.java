package util.string;

import static util.sequence.Arrayz.array;
import static util.string.Strings.requireString;
import static util.string.Strings.requireStrings;

import org.junit.Test;

public class StringsRequireTest {

    @Test(expected = IllegalArgumentException.class)
    public void requireThrowsIfNullArg() {
        requireString(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void requireThrowsIfEmptyArg() {
        requireString("");
    }
    
    @Test
    public void requireDoesntThrowIfArgHasChars() {
        requireString("1");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void requireStringsThrowsIfNullArg() {
        requireStrings(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void requireStringsThrowsIfEmptyArg() {
        requireStrings(new String[0]);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void requireStringsThrowsIfNullElement() {
        requireStrings(array(null, "1"));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void requireStringsThrowsIfEmptyElement() {
        requireStrings(array("1", ""));
    }
    
    @Test
    public void requireStringsDoesntThrowIfAllElementsHaveChars() {
        requireStrings(array("1", "12"));
    }
    
}

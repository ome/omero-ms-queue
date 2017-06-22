package util.runtime.jvm;

import static org.junit.Assert.*;
import static util.runtime.jvm.ClassPath.Separator;
import static util.sequence.Arrayz.array;

import org.junit.Test;

public class ClassPathJvmArgTest {

    @Test
    public void tokenizeDontQuoteClassPath() {
        String cp = "some path with spaces" + Separator + "and a .jar";
        
        ClassPath parsed = ClassPathFactory.fromString(cp);
        String[] actual = new ClassPathJvmArg()
                         .set(parsed)
                         .tokens()
                         .toArray(String[]::new);
        String[] expected = array("-cp", cp);
        
        assertArrayEquals(expected, actual);
    }
    
}

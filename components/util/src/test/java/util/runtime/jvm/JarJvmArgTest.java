package util.runtime.jvm;

import static org.junit.Assert.*;
import static util.sequence.Arrayz.array;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

public class JarJvmArgTest {

    @Test
    public void tokenizeDontQuoteJarPath() {
        Path jarPath = Paths.get("lib/my app.jar");
        
        String[] actual = new JarJvmArg()
                         .set(jarPath)
                         .tokens()
                         .toArray(String[]::new);
        String[] expected = array("-jar", jarPath.toString());
        
        assertArrayEquals(expected, actual);
    }
    
}

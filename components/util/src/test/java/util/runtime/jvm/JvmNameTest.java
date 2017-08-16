package util.runtime.jvm;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.nio.file.Path;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class JvmNameTest {

    private static final String OsNameKey = "os.name";
    private static String osNameValue;
    
    @BeforeClass
    public static void saveOriginalOsNameProp() {
        osNameValue = System.getProperty(OsNameKey); 
    }
    
    @AfterClass
    public static void restoreOriginalOsNameProp() {
        System.setProperty(OsNameKey, osNameValue); 
    }
    
    private static void assertName(String osName, String expectedJvmName) {
        System.setProperty(OsNameKey, osName);
        JvmName actual = JvmName.find();
        
        assertThat(actual.toString(), is(expectedJvmName));
        
        restoreOriginalOsNameProp();  // otherwise nio classes will bomb out  
        Path actualPath = actual.toPath();
        
        assertFalse(actualPath.isAbsolute());
        assertThat(actualPath.toString(), is(expectedJvmName));
    }
    
    @Test
    public void returnJavaExeIfWindows() {
        assertName("Windows 7", "java.exe");
    }
    
    @Test
    public void returnJavaIfNotWindows() {
        assertName("Arch Linux", "java");
    }

    @Test
    public void knownConstants() {
        JvmName[] vs = JvmName.values();
        assertThat(vs.length, is(2));
    }

    @Test
    public void javaConstant() {
        JvmName c = JvmName.valueOf("Java");
        assertNotNull(c);
    }

    @Test
    public void javaExeConstant() {
        JvmName c = JvmName.valueOf("JavaExe");
        assertNotNull(c);
    }
    
}

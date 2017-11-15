package util.runtime.jvm;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class JvmLocatorTest {

    private static final String JavaHomeKey = "java.home";
    private static String javaHomeValue;
    
    @BeforeClass
    public static void saveOriginalOsNameProp() {
        javaHomeValue = System.getProperty(JavaHomeKey); 
    }
    
    @AfterClass
    public static void restoreOriginalOsNameProp() {
        System.setProperty(JavaHomeKey, javaHomeValue); 
    }
    
    @Test
    public void currentJvmDirIsValueOfJavaHome() {
        System.setProperty(JavaHomeKey, "x/y");
        Path actual = JvmLocator.getCurrentJvmDir();
        
        assertThat(actual.toString(), is("x/y"));
    }
    
    @Test
    public void currentJvmExecutableNotFound() {
        System.setProperty(JavaHomeKey, "x/y");
        Optional<Path> actual = JvmLocator.findCurrentJvmExecutable();
        
        assertFalse(actual.isPresent());
    }
    
    @Test
    public void currentJvmExecutableFound() {
        Optional<Path> actual = JvmLocator.findCurrentJvmExecutable();
        
        assertTrue(actual.isPresent());
        Path java = actual.get();
        
        assertTrue(java.isAbsolute());
        assertTrue(Files.exists(java));
    }

    @Test
    public void ctor() {
        new JvmLocator();  // only to get 100% coverage.
    }
    
}

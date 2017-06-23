package util.runtime.jvm;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.Test;

public class ClassPathLocatorTest {

    @Test (expected = NullPointerException.class)
    public void findBaseThrowsIfNullArg() {
        ClassPathLocator.findBase(null);
    }
    
    @Test
    public void findBaseDir() {
        Optional<Path> actual = ClassPathLocator.findBase(getClass());
        
        assertNotNull(actual);
        assertTrue(actual.isPresent());
        
        Path basePath = actual.get();
        assertTrue(basePath.isAbsolute());
        
        String pathToClass = getClass().getName().replace('.', '/') + ".class";
        Path fullPath = basePath.resolve(pathToClass);
        
        assertTrue(Files.exists(fullPath));
    }
    
    @Test
    public void findBaseJar() {
        Optional<Path> actual = ClassPathLocator.findBase(Test.class);
        
        assertNotNull(actual);
        assertTrue(actual.isPresent());
        
        Path basePath = actual.get();
        assertTrue(basePath.isAbsolute());
        
        String junitJar = basePath.getFileName().toString().toLowerCase();
        
        assertThat(junitJar, endsWith(".jar"));
        assertThat(junitJar, containsString("junit"));
    }
    
}

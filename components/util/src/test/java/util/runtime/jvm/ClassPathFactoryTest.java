package util.runtime.jvm;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.Test;

public class ClassPathFactoryTest {

    @Test
    public void fromCurrentClassPath() {
        Optional<Path> junitJar = ClassPathFactory
                                 .fromCurrentClassPath()
                                 .toStream()
                                 .filter(p -> p.getFileName()
                                               .toString()
                                               .startsWith("junit"))
                                 .findFirst();
        
        assertTrue(junitJar.isPresent());
    }                                            
    
    @Test
    public void fromBasePath() {
        Path[] base = ClassPathFactory.fromBasePath(getClass())
                                      .toStream()
                                      .toArray(Path[]::new);
        assertThat(base.length, is(1));
        assertTrue(Files.exists(base[0]));
    }
    
    @Test
    public void fromLibDirReturnsEmptyClassPathIfNoJarsFound() throws IOException {
        Path base = ClassPathLocator.findBase(getClass()).get();
        String relPathToThisClass = getClass().getName().replace('.', '/');
        Path pathToParentDir = base.resolve(Paths.get(relPathToThisClass))
                                   .getParent();
        ClassPath actual = ClassPathFactory.fromLibDir(pathToParentDir);
        
        assertTrue(actual.isEmpty());
    }

    @Test
    public void fromDir() throws IOException {
        Path base = ClassPathLocator.findBase(getClass()).get();
        String relPathToThisClass = getClass().getName().replace('.', '/');
        Path pathToParentDir = base.resolve(Paths.get(relPathToThisClass))
                                   .getParent();
        String actual = ClassPathFactory
                       .fromDir(pathToParentDir,
                                file -> file.toString().endsWith(".class"))
                       .toString();
        assertThat(actual, containsString(relPathToThisClass));
    }

    @Test (expected = NullPointerException.class)
    public void fromBasePathThrowsIfNullArg() {
        ClassPathFactory.fromBasePath(null);
    }
    
    @Test (expected = NullPointerException.class)
    public void fromLibDirThrowsIfNullArg() throws IOException {
        ClassPathFactory.fromLibDir(null);
    }
    
    @Test (expected = NullPointerException.class)
    public void fromDirThrowsIfNullFirstArg() throws IOException {
        ClassPathFactory.fromDir(null, x -> true);
    }
    
    @Test (expected = NullPointerException.class)
    public void fromDirThrowsIfNullSecondArg() throws IOException {
        ClassPathFactory.fromDir(Paths.get(""), null);
    }

    @Test
    public void ctor() {
        new ClassPathFactory();  // only to get 100% coverage.
    }

}

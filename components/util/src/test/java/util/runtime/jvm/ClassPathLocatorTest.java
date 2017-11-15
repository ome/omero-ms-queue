package util.runtime.jvm;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.net.URL;
import java.nio.file.FileSystemNotFoundException;
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

    @Test (expected = NullPointerException.class)
    public void toPathThrowsIfNullResource() {
        ClassPathLocator.toPath(null);
    }

    @Test
    public void toPathReturnsEmptyIfHttpResource() throws Exception {
        URL webFile = new URL("http://host/file");
        Optional<Path> actual = ClassPathLocator.toPath(webFile);

        assertNotNull(actual);
        assertFalse(actual.isPresent());
    }

    @Test
    public void toPathReturnsEmptyIfFtpResource() throws Exception {
        URL file = new URL("ftp://host/file");
        Optional<Path> actual = ClassPathLocator.toPath(file);

        assertNotNull(actual);
        assertFalse(actual.isPresent());
    }

    @Test
    public void toPathReturnsPathEvenIfFileResourceDoesntExist()
            throws Exception {
        String nonExistingPath = "/dev/null/not/there";
        URL file = new URL("file://" + nonExistingPath);
        Optional<Path> actual = ClassPathLocator.toPath(file);

        assertNotNull(actual);
        assertTrue(actual.isPresent());
        assertThat(actual.get().toString(), is(nonExistingPath));
    }

    @Test
    public void toPathReturnsPathEvenIfJarResourceDoesntExist()
            throws Exception {
        String nonExistingPath = "/dev/null/not/there";
        URL file = new URL("jar:file:" + nonExistingPath + "!/");
        Optional<Path> actual = ClassPathLocator.toPath(file);

        assertNotNull(actual);
        assertTrue(actual.isPresent());
        assertThat(actual.get().toString(), is(nonExistingPath));
    }

    @Test
    public void toPathCanHandleNestedClassDir() throws Exception {
        URL jar = new URL(
                "jar:file:/my/libs/ome-smuggler-1.0.0.jar!/BOOT-INF/classes!/");
        Optional<Path> actual = ClassPathLocator.toPath(jar);

        assertNotNull(actual);
        assertTrue(actual.isPresent());
        assertThat(actual.get().toString(),
                   is("/my/libs/ome-smuggler-1.0.0.jar"));
    }

    @Test
    public void toPathThrowsIfNonLocalJar() throws Exception {
        URL jar = new URL("jar:file://host/my/lib.jar!/");
        try {
            ClassPathLocator.toPath(jar);
            fail("expected IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                       containsString("URI has an authority component"));
        }
    }

    @Test
    public void toPathThrowsIfHttpLocalJar() throws Exception {
        URL jar = new URL("jar:http://host/my/lib.jar!/");
        try {
            ClassPathLocator.toPath(jar);
            fail("expected FileSystemNotFoundException.");
        } catch (FileSystemNotFoundException e) {
            assertThat(e.getMessage(),
                       containsString("Provider \"http\" not installed"));
        }
    }

    @Test
    public void ctor() {
        new ClassPathLocator();  // only to get 100% coverage.
    }

}

package util.runtime.jvm;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.runtime.jvm.ClassPath.Separator;
import static util.sequence.Arrayz.array;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;


@RunWith(Theories.class)
public class ClassPathTest {

    @DataPoints
    public static Path[] paths = Stream.of("", "x", "y", "z")
                                       .map(Paths::get)
                                       .toArray(Path[]::new);
    
    @DataPoints
    public static String[] classpaths = array(
            "x1/x2", 
            "x.jar" + Separator + "y.jar", 
            "x" + Separator + "y1/y2" + Separator + "z");
    
    @Theory
    public void fromStringYieldsSameClassPathAsThatBuiltByAppendingPaths(
            Path p1, Path p2, Path p3) {
        String cp = p1 + Separator + p2 + Separator + p3;
        ClassPath parsed = ClassPathFactory.fromString(cp);
        ClassPath built = new ClassPath().add(p1, p2, p3);
        
        assertNotNull(parsed);
        assertThat(parsed, is(built));
        
        String parsedToString = parsed.toString();
        String builtToString = built.toString();
        assertNotNull(parsedToString);
        assertNotNull(builtToString);        
        assertThat(parsedToString, is(builtToString));
        
        Stream<Path> parsedToStream = parsed.toStream();
        Stream<Path> builtToStream = built.toStream();
        assertNotNull(parsedToStream);
        assertNotNull(builtToStream);
        assertArrayEquals(parsedToStream.toArray(), builtToStream.toArray());
    }
    
    @Theory
    public void addOrderIsRespected(Path p1, Path p2, Path p3) {
        String[] actual = new ClassPath()
                         .add(p1, p2, p3)
                         .toStream()
                         .map(Path::toString)
                         .toArray(String[]::new);
        
        LinkedHashSet<String> ps = new LinkedHashSet<>();  // respects add order
        ps.add(p1.toString());
        ps.add(p2.toString());
        ps.add(p3.toString());
        ps.remove("");
        String[] expected = ps.toArray(new String[0]);
        
        assertArrayEquals(expected, actual);
    }
    
    @Theory
    public void fromStringToStringIsIdentity(String classpath) {
        String actual = ClassPathFactory.fromString(classpath).toString();
        assertThat(actual, is(classpath));
    }
    
    @Theory
    public void clonedClassPathIsSameAsOriginal(
            Path p1, Path p2, Path p3) {
        ClassPath original = new ClassPath().add(p1, p2, p3);
        ClassPath cloned = new ClassPath().add(original);
        
        assertThat(cloned, is(original));
    }
    
    @Test
    public void isEmptyAfterInitialization() {
        assertTrue(new ClassPath().isEmpty());
    }
    
    @Test
    public void isEmptyAfterAddingEmptyPath() {
        assertTrue(new ClassPath().add(Paths.get("")).isEmpty());
    }
    
    @Test
    public void isNotEmptyAfterAddingNonEmptyPath() {
        assertFalse(new ClassPath().add(Paths.get("x")).isEmpty());
    }
    
    @Test(expected = NullPointerException.class)
    public void addThrowsIfNullStream() {
        new ClassPath().add((Stream<Path>)null);
    }
    
    @Test(expected = NullPointerException.class)
    public void addThrowsIfStreamHasNullElements() {
        new ClassPath().add(Stream.of(Paths.get("x"), null));
    }
    
    @Test(expected = NullPointerException.class)
    public void addThrowsIfNullPathArray() {
        new ClassPath().add((Path[])null);
    }
    
    @Test(expected = NullPointerException.class)
    public void addThrowsIfPathArrayHasNullElements() {
        new ClassPath().add(array(Paths.get("x"), null));
    }
    
    @Test(expected = NullPointerException.class)
    public void addThrowsIfNullClassPath() {
        new ClassPath().add((ClassPath)null);
    }
    
}

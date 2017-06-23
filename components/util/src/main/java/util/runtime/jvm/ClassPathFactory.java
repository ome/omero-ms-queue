package util.runtime.jvm;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Methods to build class paths.
 */
public class ClassPathFactory {

    /**
     * Parses the given string as a JVM class path.
     * @param cp a string of paths separated by a {@link ClassPath#Separator}. 
     * It may be empty.
     * @return the parsed class path entries.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static ClassPath fromString(String cp) {
        requireNonNull(cp, "cp");
        
        String[] cpEntries = cp.split(ClassPath.Separator);
        Stream<Path> ps = Stream.of(cpEntries).map(Paths::get);
        return new ClassPath().add(ps);
    }

    /**
     * Parses the current class path string into a JVM class path.
     * The current class path is that with which this running JVM was launched, 
     * as returned by the {@code java.class.path} system property.
     * @return the parsed class path entries.
     */
    public static ClassPath fromCurrentClassPath() {
        String cp = Optional.ofNullable(System.getProperty("java.class.path"))
                            .orElse("");  // (*)
        return fromString(cp);
    }
    // (*) should never be null, but rather be safe than sorry...
    
    /**
     * Builds a class path containing the base path from which the given class
     * was loaded.
     * This method uses the {@link ClassPathLocator#findBase(Class) 
     * ClassPathLocator.findBase} method to determine the jar file or directory
     * from which the class was loaded; therefore, the class is assumed to have
     * been loaded from a local directory or jar file.
     * @param classInBasePath the class to use for determining the base path.
     * @return a class path containing the given class base path if it could be
     * determined to be that of a local directory or jar file; an empty optional
     * otherwise.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws SecurityException if a security manager exists and its 
     * the application doesn't have the "getProtectionDomain" permission.
     */
    public static ClassPath fromBasePath(Class<?> classInBasePath) {
        return ClassPathLocator.findBase(classInBasePath)
                               .map(p -> new ClassPath().add(p))
                               .orElse(new ClassPath());
    }
    
    /**
     * Builds a class path with all the jar files found in the given directory
     * and, recursively, in all of its sub-directories.
     * @param dir the directory in which to find the jar files.
     * @return the class path.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws IOException if an I/O error occurs while trawling the directory.
     */
    public static ClassPath fromLibDir(Path dir) throws IOException {
        return fromDir(dir, file -> file.toString().endsWith(".jar"));
    }
    
    /**
     * Builds a class path with all the files selected by the given predicate 
     * in the specified base directory and, recursively, in all of its 
     * sub-directories.
     * @param dir the directory in which to find the (jar) files.
     * @param select tests whether or not to include a regular file.
     * @return the class path.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws IOException if an I/O error occurs while trawling the directory.
     */
    public static ClassPath fromDir(Path dir, Predicate<Path> select) 
            throws IOException {
        requireNonNull(dir, "dir");
        requireNonNull(select, "select");
        
        ClassPath cp = new ClassPath();
        
        Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file,
                    BasicFileAttributes attrs) throws IOException {
                if (attrs.isRegularFile() && select.test(file)) {
                    cp.add(file);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        
        return cp;
    }
    
}

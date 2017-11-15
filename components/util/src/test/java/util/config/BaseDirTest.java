package util.config;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.junit.Test;

public class BaseDirTest {

    private static final String key = "non-existing";
    
    private static BaseDir newBaseDir() {
        return new BaseDir(key, key);
    }
    
    private static void setProp(Path dir) {
        BaseDir.store(key, dir);
    }
    
    private static void unsetProp() {
        System.setProperty(key, "");
    }
    
    private static Path pwd(String...ps) {
        return Paths.get(System.getProperty("user.dir"), ps);
    }
    
    @Test
    public void defaultToPwd() {
        assertThat(newBaseDir(), is(pwd()));
    }

    @Test
    public void useAsIsIfSet() {
        Path dir = Paths.get("rel", "path");
        setProp(dir);
        
        assertThat(newBaseDir(), is(dir));
        
        unsetProp();
    }

    @Test
    public void resolveNullConfigPathToBase() {
        Path actual = newBaseDir().resolve(null);
        assertThat(actual, is(pwd()));
    }

    @Test
    public void resolveEmptyConfigPathToBase() {
        Path actual = newBaseDir().resolve("");
        assertThat(actual, is(pwd()));
    }
    
    @Test
    public void resolveAbsoluteConfigPathToSelf() {
        Path absPath = pwd("x").toAbsolutePath();
        Path actual = newBaseDir().resolve(absPath.toString());
        assertThat(actual, is(absPath));
    }
    
    @Test
    public void resolveRelativeConfigPathAgainstBaseDir() {
        Path actual = newBaseDir().resolve("x");
        assertThat(actual, is(pwd("x")));
    }

    @Test
    public void resolveRequiredPathAgainstBaseDir() {
        Path actual = newBaseDir().resolveRequiredPath("x");
        assertThat(actual, is(pwd("x")));
    }

    @Test
    public void createAndSavePathToTempDir() throws IOException {
        String key = UUID.randomUUID().toString();
        String prefix = "prefix";
        Path tmpDirPath = BaseDir.storeTempDir(key, prefix);
        String tmpDirPropValue = System.getProperty(key);

        assertNotNull(tmpDirPath);
        assertTrue(Files.exists(tmpDirPath));
        assertTrue(Files.isDirectory(tmpDirPath));

        assertNotNull(tmpDirPropValue);
        assertThat(tmpDirPropValue, containsString(prefix));
    }

    @Test (expected = IllegalArgumentException.class)
    public void resolveRequiredPathThrowsIfNullPath() {
        newBaseDir().resolveRequiredPath(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void resolveRequiredPathThrowsIfEmptyPath() {
        newBaseDir().resolveRequiredPath("");
    }

    @Test (expected = IllegalArgumentException.class)
    public void storeTempDirThrowsIfNullKey() throws IOException {
        BaseDir.storeTempDir(null, "/");
    }

    @Test (expected = IllegalArgumentException.class)
    public void storeTempDirThrowsIfEmptyKey() throws IOException {
        BaseDir.storeTempDir("", "/");
    }

    @Test (expected = IllegalArgumentException.class)
    public void storeTempDirThrowsIfNullPrefix() throws IOException {
        BaseDir.storeTempDir("key", null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void storeTempDirThrowsIfEmptyPrefix() throws IOException {
        BaseDir.storeTempDir("key", "");
    }

}

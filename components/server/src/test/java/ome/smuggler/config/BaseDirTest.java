package ome.smuggler.config;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.nio.file.Path;
import java.nio.file.Paths;

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
    
}

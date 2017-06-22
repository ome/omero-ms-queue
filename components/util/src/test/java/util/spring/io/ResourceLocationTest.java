package util.spring.io;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class ResourceLocationTest {

    @Test(expected = IllegalArgumentException.class)
    public void ctorThrowsIfNullPath() {
        ResourceLocation.filepath((Object[])null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void ctorThrowsIfEmptyPath() {
        ResourceLocation.filepath();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void ctorThrowsIfPathHasNulls() {
        ResourceLocation.filepath("", null, "");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void ctorThrowsIfAllPathComponentsEmpty() {
        ResourceLocation.filepath("");
    }
    
    @Test
    public void emptiesAreFilteredOut() {
        String actual = ResourceLocation.filepath("", "1", "", "2", "").get();
        String expected = "file:/1/2";
        
        assertThat(actual, is(expected));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void relpathNoComponents() {
        ResourceLocation.relpath();
    }
    
    @Test
    public void relpathOneComponent() {
        String actual = ResourceLocation.relpath("1").get();
        String expected = "1";
        
        assertThat(actual, is(expected));
    }
    
    @Test
    public void relpathTwoComponents() {
        String actual = ResourceLocation.relpath("1", "2").get();
        String expected = "1/2";
        
        assertThat(actual, is(expected));
    }
    
    @Test
    public void filepathOneComponent() {
        String actual = ResourceLocation.filepath("1").get();
        String expected = "file:/1";
        
        assertThat(actual, is(expected));
    }
    
    @Test
    public void filepathTwoComponents() {
        String actual = ResourceLocation.filepath("1", "2").get();
        String expected = "file:/1/2";
        
        assertThat(actual, is(expected));
    }
    
    @Test
    public void filepathFromCwdOneComponent() {
        String actual = ResourceLocation.filepathFromCwd("1").get();
        String expected = "file:./1";
        
        assertThat(actual, is(expected));
    }
    
    @Test
    public void filepathFromCwdTwoComponents() {
        String actual = ResourceLocation.filepathFromCwd("1", "2").get();
        String expected = "file:./1/2";
        
        assertThat(actual, is(expected));
    }
    
    @Test
    public void classpathOneComponent() {
        String actual = ResourceLocation.classpath("1").get();
        String expected = "classpath:/1";
        
        assertThat(actual, is(expected));
    }
    
    @Test
    public void classpathTwoComponents() {
        String actual = ResourceLocation.classpath("1", "2").get();
        String expected = "classpath:/1/2";
        
        assertThat(actual, is(expected));
    }
    
}

package ome.smuggler.run;

import static org.junit.Assert.*;
import static util.sequence.Arrayz.array;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ome.smuggler.config.Profiles;

import org.junit.Test;

public class ImportServerTest {

    @Test
    public void useProdProfileIfNullArgs() {
        String[] actual = new ImportServer().getProfiles(null);
        String[] expected = array(Profiles.Prod);
        
        assertNotNull(actual);
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void useProdProfileIfEmptyArgs() {
        String[] actual = new ImportServer().getProfiles(Collections.emptyList());
        String[] expected = array(Profiles.Prod);
        
        assertNotNull(actual);
        assertArrayEquals(expected, actual);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void throwIfBadProfiles() {
        List<String> bad = Arrays.asList(Profiles.Prod, Profiles.Prod + "*");
        new ImportServer().getProfiles(bad);
    }
    
    @Test
    public void returnSameIfValidProfileArg() {
        String[] expected = array(Profiles.QA);
        String[] actual = new ImportServer().getProfiles(Arrays.asList(expected));
        
        assertNotNull(actual);
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void returnSameIfValidProfileArgs() {
        String[] expected = array(Profiles.QA, Profiles.Prod);
        String[] actual = new ImportServer().getProfiles(Arrays.asList(expected));
        
        
        assertNotNull(actual);
        assertArrayEquals(expected, actual);
    }
    
}

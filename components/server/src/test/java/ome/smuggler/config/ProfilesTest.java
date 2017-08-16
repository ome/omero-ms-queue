package ome.smuggler.config;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.sequence.Arrayz.array;

import java.util.stream.Stream;

import org.junit.Test;

public class ProfilesTest {

    private static void assertEmpty(Stream<String> actual) {
        assertNotNull(actual);
        assertThat(actual.count(), is(0L));
    }
    
    @Test
    public void findUnknownProfilesReturnsEmptyIfNullArg() {
        Stream<String> actual = Profiles.findUnknownProfiles(null);
        assertEmpty(actual);
    }
    
    @Test
    public void findUnknownProfilesReturnsEmptyIfEmptyArg() {
        Stream<String> actual = Profiles.findUnknownProfiles(Stream.empty());
        assertEmpty(actual);
    }
    
    @Test
    public void findUnknownProfilesReturnsEmptyIfAllKnownProfiles() {
        Stream<String> actual = Profiles.findUnknownProfiles(
                                    Stream.of(Profiles.Dev, Profiles.Prod));
        assertEmpty(actual);
    }
    
    @Test
    public void findUnknownProfilesReturnsBadProfileNames() {
        String[] expected = array(Profiles.Dev + "*", Profiles.Prod + "*");
        
        String[] actual = Profiles.findUnknownProfiles(
                                                Stream.of(Profiles.Dev,
                                                          expected[0],
                                                          Profiles.Prod,
                                                          expected[1]))
                                  .toArray(String[]::new);
        
        assertArrayEquals(expected, actual);
    }

    @Test
    public void ctor() {
        new Profiles();  // only to get 100% coverage.
    }
    
}

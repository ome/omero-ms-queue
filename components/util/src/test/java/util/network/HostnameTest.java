package util.network;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.net.InetAddress;
import java.util.Optional;

import org.junit.Test;


public class HostnameTest {

    private static String hostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return Hostname.UnknownHost;
        }
    }


    @Test
    public void returnEmptyIfLookupFails() {
        Optional<String> actual = Hostname.lookup(
                () -> { throw new Exception(); });

        assertNotNull(actual);
        assertThat(actual, is(Optional.empty()));
    }

    @Test
    public void returnResultIfLookupSucceeds() {
        String name = "name";
        Optional<String> actual = Hostname.lookup(() -> name);

        assertNotNull(actual);
        assertTrue(actual.isPresent());
        assertThat(actual.get(), is(name));
    }

    @Test
    public void builtinLookup() {
        String expected = hostname();
        String actual = Hostname.lookup();

        assertThat(actual, is(expected));
    }

    @Test
    public void ctor() {
        new Hostname();  // only to get 100% coverage.
    }

}

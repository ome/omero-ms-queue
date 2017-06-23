package ome.smuggler.core.types;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.sequence.Arrayz.array;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import util.object.Wrapper;

import java.net.URI;

@RunWith(Theories.class)
public class RemoteFilePathTest {

    static class Scheme extends Wrapper<String> {
        public Scheme(String wrappedValue) {
            super(wrappedValue);
        }
    }

    static class Host extends Wrapper<String> {
        public Host(String wrappedValue) {
            super(wrappedValue);
        }
    }

    @DataPoints
    public static final Scheme[] schemeSupply = array(
        new Scheme(""), new Scheme("xxx"), new Scheme("ftp"), new Scheme("file")
    );

    @DataPoints
    public static final Host[] hostSupply = array(
        new Host(""), new Host("host")
    );

    @DataPoints
    public static final String[] pathSupply = array(
        "", "/", "/x/y"
    );

    private static URI buildURI(String scheme, String host, String path) {
        StringBuilder buf = new StringBuilder();

        if (!scheme.isEmpty()) {
            buf.append(scheme);
            buf.append("://");
        }
        if (host.isEmpty() && path.isEmpty()) {
            buf.append("/");
        }
        buf.append(host);
        buf.append(path);

        return URI.create(buf.toString());
    }


    @Theory
    public void isRemoteJustInCaseIsFileSchemeAndHasHost(
            Scheme s, Host h, String path) {
        String scheme = s.get(), host = h.get();
        URI x = buildURI(scheme, host, path);

        boolean expected = "file".equals(scheme) && !host.isEmpty();
        boolean actual = RemoteMount.isRemoteFilePath(x);

        assertThat(actual, is(expected));
    }

    @Test
    public void nullNotRemotePath() {
        assertFalse(RemoteMount.isRemoteFilePath(null));
    }

    @Test
    public void emptyNotRemotePath() {
        URI x = URI.create("");
        assertFalse(RemoteMount.isRemoteFilePath(x));
    }

    @Test
    public void absLocalPathNotRemotePath() {
        URI x = URI.create("/some/file");
        assertFalse(RemoteMount.isRemoteFilePath(x));
    }

    @Test
    public void relLocalPathNotRemotePath() {
        URI x = URI.create("some/file");
        assertFalse(RemoteMount.isRemoteFilePath(x));
    }

    @Test
    public void urlNotRemotePath() {
        URI x = URI.create("http://host/some/file");
        assertFalse(RemoteMount.isRemoteFilePath(x));
    }

    @Test
    public void fileUriWithoutHostNotRemotePath() {
        URI x = URI.create("file:///some/file");
        assertFalse(RemoteMount.isRemoteFilePath(x));
    }

    @Test
    public void fileUriWithHostIsRemotePath() {
        URI x = URI.create("file://host");
        assertTrue(RemoteMount.isRemoteFilePath(x));
    }

    @Test
    public void fileUriWithHostAndPathIsRemotePath() {
        URI x = URI.create("file://host/some/file");
        assertTrue(RemoteMount.isRemoteFilePath(x));
    }

}

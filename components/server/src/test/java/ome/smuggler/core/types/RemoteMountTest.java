package ome.smuggler.core.types;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import static util.sequence.Arrayz.array;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import util.object.Wrapper;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RunWith(Theories.class)
public class RemoteMountTest {

    static class RemoteBase extends Wrapper<String> {
        public RemoteBase(String wrappedValue) {
            super(wrappedValue);
        }
    }

    static class LocalBase extends Wrapper<String> {
        public LocalBase(String wrappedValue) {
            super(wrappedValue);
        }
    }

    private static URI uri(String x) {
        return URI.create(x);
    }

    private static URI uri(RemoteBase x) {
        return URI.create(x.get());
    }

    private static Path path(String absPath) {
        return Paths.get(uri("file://" + absPath));
    }
    // NB converting to URI to avoid being platform dependent with '/' in path.

    private static Path path(LocalBase x) {
        return path(x.get());
    }

    @DataPoints
    public static final RemoteBase[] remoteBaseSupply = array(
        new RemoteBase("file://h"), new RemoteBase("file://h/"),
        new RemoteBase("file://h1/d"), new RemoteBase("file://h1/d/")
    );

    @DataPoints
    public static final LocalBase[] localBaseSupply = array(
        new LocalBase("/"), new LocalBase("/mnt"),
        new LocalBase("/mnt/d"), new LocalBase("/mnt/d/")
    );

    @DataPoints
    public static final String[] relPathSupply = array("", "x", "x/y", "x/y/");


    private static String join(String base, String rest) {
        return base.endsWith("/") ? base + rest
                                  : base + "/" + rest;
    }

    private static Path join(LocalBase base, String rest) {
        return path(join(base.get(), rest));
    }

    private static URI join(RemoteBase base, String rest) {
        return uri(join(base.get(), rest));
    }

    @Theory
    public void nullNeverTranslatesToLocalPath(
            RemoteBase remote, LocalBase local) {
        RemoteMount target = new RemoteMount(uri(remote), path(local));

        Optional<Path> actual = target.toLocalPath(null);
        assertFalse(actual.isPresent());
    }

    @Theory
    public void localPathNeverTranslatesToLocalPath(
            RemoteBase remote, LocalBase local, String path) {
        RemoteMount target = new RemoteMount(uri(remote), path(local));

        Optional<Path> actual = target.toLocalPath(uri(path));
        assertFalse(actual.isPresent());
    }

    @Theory
    public void translateRemotePathIfSameRemotePrefix(
            RemoteBase remote, LocalBase local, String path) {
        RemoteMount target = new RemoteMount(uri(remote), path(local));
        URI input = join(remote, path);
        Path expected = join(local, path);

        Optional<Path> actual = target.toLocalPath(input);
        assertTrue(actual.isPresent());
        assertThat(actual.get(), is(expected));
    }

    @Theory
    public void neverTranslateRemotePathIfNotSameRemotePrefix(
            RemoteBase remote, LocalBase local, String path) {
        assumeThat(remote.get(), not(endsWith("/")));

        RemoteMount target = new RemoteMount(uri(remote), path(local));
        RemoteBase otherRemote = new RemoteBase(remote.get() + "different");
        URI input = join(otherRemote, path);

        Optional<Path> actual = target.toLocalPath(input);
        assertFalse(actual.isPresent());
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullRemoteBase() {
        new RemoteMount(null, Paths.get("/"));
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullLocalBase() {
        new RemoteMount(URI.create("/"), null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void ctorThrowsIfNotRemotePath() {
        new RemoteMount(URI.create("/"), Paths.get("/"));
    }

}

package ome.smuggler.core.types;

import static ome.smuggler.core.types.ValueParserFactory.email;
import static ome.smuggler.core.types.ValueParserFactory.uri;
import static org.junit.Assert.*;

import org.junit.Test;
import util.object.Pair;

import java.net.URI;
import java.util.stream.Stream;

public class ImportBatchTest {

    public static ImportInput makeNewImportInput() {
        return makeNewImportInput("target/file");
    }

    public static ImportInput makeNewImportInput(String targetUri) {
        return new ImportInput(email("user@micro.edu").getRight(),
                uri(targetUri).getRight(),
                uri("omero:1234").getRight(),
                "sessionKey");
    }

    public static ImportBatch makeNew() {
        return new ImportBatch(Stream.of(makeNewImportInput(),
                                         makeNewImportInput()));
    }

    public static ImportBatch makeNew(String...ts) {
        return new ImportBatch(
                Stream.of(ts).map(ImportBatchTest::makeNewImportInput));
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullImportStream() {
        new ImportBatch(null);
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullImports1() {
        new ImportBatch(Stream.of(null, null));
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullImports2() {
        new ImportBatch(Stream.of(makeNewImportInput(), null));
    }

    @Test (expected = IllegalArgumentException.class)
    public void ctorThrowsIfEmptyImportStream() {
        new ImportBatch(Stream.empty());
    }

    @Test
    public void hasBatchId() {
        assertNotNull(makeNew().batchId());
    }

    @Test
    public void hasImports() {
        assertNotNull(makeNew().imports());
    }

    @Test
    public void importIdsAreAllDifferent() {
        ImportId[] xs = makeNew("/a", "/a")
                       .identifyTargets()
                       .map(Pair::fst)
                       .toArray(ImportId[]::new);
        assertNotEquals(xs[0], xs[1]);
    }

    @Test
    public void associateImportIdsToTargetUris() {
        URI[] xs = makeNew("/a", "/b")
                  .identifyTargets()
                  .map(Pair::snd)
                  .toArray(URI[]::new);
        assertNotEquals(xs[0], xs[1]);
    }

}

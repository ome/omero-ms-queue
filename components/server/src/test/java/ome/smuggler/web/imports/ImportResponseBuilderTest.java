package ome.smuggler.web.imports;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static java.util.stream.Collectors.toSet;
import static util.string.Strings.*;
import static ome.smuggler.core.service.imports.impl.Utils.newImportBatch;

import java.net.URI;
import java.nio.file.Paths;
import java.util.Set;

import ome.smuggler.core.types.*;
import org.junit.Before;
import org.junit.Test;
import util.object.Pair;


public class ImportResponseBuilderTest {

    private static void assertStatusUri(ImportResponse r, Set<String> importIds) {
        assertFalse(isNullOrEmpty(r.statusUri));

        URI status = URI.create(r.statusUri);
        assertFalse(status.isAbsolute());

        assertTrue(status.getPath().startsWith("/"));

        String id = Paths.get(status.getPath()).getFileName().toString();
        assertTrue(importIds.contains(id));
    }

    private static void assertResponse(ImportResponse r, Set<String> importIds) {
        assertNotNull(r);
        assertFalse(isNullOrEmpty(r.targetUri));
        assertStatusUri(r, importIds);
    }

    private static void assertResponses(ImportResponse[] actual,
                                        ImportBatch submitted) {
        assertNotNull(actual);
        assertThat((long) actual.length, is(submitted.imports().count()));

        Set<String> importIds = submitted.identifyTargets()
                                         .map(Pair::fst)
                                         .map(ImportId::get)
                                         .collect(toSet());
        for (ImportResponse r : actual) {
            assertResponse(r, importIds);
        }
    }


    private ImportResponseBuilder target;

    @Before
    public void setup() {
        target = new ImportResponseBuilder();
    }

    @Test(expected = NullPointerException.class)
    public void buildThrowsIfNullBatch() {
        target.build(null);
    }

    @Test
    public void buildOneResponse() {
        ImportBatch batch = newImportBatch("1");
        ImportResponse[] actual = target.build(batch);
        assertResponses(actual, batch);
    }

    @Test
    public void buildManyResponses() {
        ImportBatch batch = newImportBatch("1", "2");
        ImportResponse[] actual = target.build(batch);
        assertResponses(actual, batch);
    }

}

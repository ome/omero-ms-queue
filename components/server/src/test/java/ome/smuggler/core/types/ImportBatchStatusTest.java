package ome.smuggler.core.types;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import static ome.smuggler.core.types.ProcessedImport.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.Set;
import java.util.stream.IntStream;


@RunWith(Theories.class)
public class ImportBatchStatusTest {

    static final int numImports = 4;

    static ProcessedImport importNotInBatch() {
        QueuedImport qi = new QueuedImport(new ImportId(new ImportBatchId()),
                ImportBatchTest.makeNewImportInput());
        return succeeded(qi);
    }

    static void markCompletions(ImportBatchStatus status,
                                int nSuccess, int nFails) {
        status.batch().imports().limit(nSuccess)
                .map(ProcessedImport::succeeded)
                .forEach(status::addToCompleted);
        status.batch().imports().skip(nSuccess).limit(nFails)
                .map(ProcessedImport::failed)
                .forEach(status::addToCompleted);
    }

    static boolean isPartition(int x, int y) {
        return x + y == numImports;
    }

    static boolean isBelowPartition(int x, int y) {
        return x + y < numImports;
    }

    @DataPoints
    public static int[] genSupply =
            IntStream.rangeClosed(0, numImports).toArray();


    private ImportBatchStatus target;


    private QueuedImport firstImportInBatch() {
        return target.batch().imports().findFirst().get();
    }

    @Before
    public void setup() {
        String[] files = IntStream.rangeClosed(1, numImports)
                                  .mapToObj(n -> "file/" + n)
                                  .toArray(String[]::new);
        ImportBatch batch = ImportBatchTest.makeNew(files);
        target = new ImportBatchStatus(batch);
    }

    void assertSuccessAndFailSetsAreDisjoint(int nSuccess, int nFails) {
        Set<QueuedImport> succeeded = target.succeeded(),
                            failed = target.failed();

        assertThat(succeeded.size(), is(nSuccess));
        assertThat(failed.size(), is(nFails));

        succeeded.retainAll(failed);  // intersection now in succeeded
        assertThat(succeeded.size(), is(0));

    }

    @Theory
    public void allProcessedTrueIfAllImportsBeenAdded(int nSuccess, int nFails) {
        assumeTrue(isPartition(nSuccess, nFails));

        markCompletions(target, nSuccess, nFails);

        assertTrue(target.allProcessed());
        assertSuccessAndFailSetsAreDisjoint(nSuccess, nFails);
    }

    @Theory
    public void allProcessedFalseIfSomeImportsNotBeenAdded(int nSuccess, int nFails) {
        assumeTrue(isBelowPartition(nSuccess, nFails));

        markCompletions(target, nSuccess, nFails);

        assertFalse(target.allProcessed());
        assertSuccessAndFailSetsAreDisjoint(nSuccess, nFails);
    }

    @Theory
    public void allSucceededImpliesAllProcessed(int nSuccess, int nFails) {
        assumeTrue(isPartition(nSuccess, nFails));
        assumeTrue(nFails == 0);

        markCompletions(target, nSuccess, nFails);

        assertTrue(target.allSucceeded());
        assertTrue(target.allProcessed());
    }

    @Theory
    public void allProcessedDontImplyAllSucceeded(int nSuccess, int nFails) {
        assumeTrue(isPartition(nSuccess, nFails));
        assumeTrue(nFails > 0);

        markCompletions(target, nSuccess, nFails);

        assertFalse(target.allSucceeded());
        assertTrue(target.allProcessed());
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullArg() {
        new ImportBatchStatus(null);
    }

    @Test (expected = NullPointerException.class)
    public void addToCompletedThrowsIfNullArg() {
        target.addToCompleted(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void addToCompletedThrowsIfImportNotInBatch() {
        target.addToCompleted(importNotInBatch());
    }

    @Test (expected = IllegalArgumentException.class)
    public void addToSucceededThrowsIfImportAlreadyAddedToFailed() {
        QueuedImport qi = firstImportInBatch();
        try {
            target.addToCompleted(failed(qi));
        } catch (Exception e) {
            fail(e.getMessage());
        }
        target.addToCompleted(succeeded(qi));
    }

    @Test (expected = IllegalArgumentException.class)
    public void addToFailedThrowsIfImportAlreadyAddedToSucceeded() {
        QueuedImport qi = firstImportInBatch();
        try {
            target.addToCompleted(succeeded(qi));
        } catch (Exception e) {
            fail(e.getMessage());
        }

        target.addToCompleted(failed(qi));
    }

    @Test
    public void canReAddSameImportToSucceededSet() {
        QueuedImport qi = firstImportInBatch();
        target.addToCompleted(succeeded(qi));
        target.addToCompleted(succeeded(qi));

        assertThat(target.succeeded().size(), is(1));
        assertThat(target.failed().size(), is(0));
        assertTrue(target.succeeded().contains(qi));
    }

    @Test
    public void canReAddSameImportToFailedSet() {
        QueuedImport qi = firstImportInBatch();
        target.addToCompleted(failed(qi));
        target.addToCompleted(failed(qi));

        assertThat(target.failed().size(), is(1));
        assertThat(target.succeeded().size(), is(0));
        assertTrue(target.failed().contains(qi));
    }

}

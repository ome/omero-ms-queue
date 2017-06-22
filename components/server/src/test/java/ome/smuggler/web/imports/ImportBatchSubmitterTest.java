package ome.smuggler.web.imports;

import ome.smuggler.core.service.imports.ImportRequestor;
import ome.smuggler.core.types.ImportInput;
import ome.smuggler.web.Error;
import org.junit.Before;
import org.junit.Test;
import util.object.Either;

import java.util.stream.Stream;

import static ome.smuggler.core.service.imports.impl.Utils.newImportBatch;
import static ome.smuggler.web.imports.Utils.newImportRequests;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static util.sequence.Arrayz.hasNulls;

public class ImportBatchSubmitterTest {

    private ImportRequestor serviceMock;
    private ImportBatchSubmitter target;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        serviceMock = mock(ImportRequestor.class);
        target = new ImportBatchSubmitter(serviceMock);

        when(serviceMock.enqueue((Stream<ImportInput>) any()))
                .thenReturn(newImportBatch("1", "2"));
    }

    @SuppressWarnings("unchecked")
    private void verifyServiceCalled(int n) {
        verify(serviceMock, times(n)).enqueue((Stream<ImportInput>) any());
    }

    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfNullService() {
        new ImportBatchSubmitter(null);
    }

    @Test
    public void neverHitServiceIfNullRequests() {
        target.submit((ImportRequest[]) null);
        verifyServiceCalled(0);
    }

    @Test
    public void neverHitServiceIfRequestsAllNull() {
        target.submit(null, null);
        verifyServiceCalled(0);
    }

    @Test
    public void neverHitServiceIfSomeRequestsFailValidation() {
        ImportRequest[] rs = newImportRequests(2);
        rs[1].sessionKey = null;  // will fail validation
        target.submit(rs);
        verifyServiceCalled(0);
    }

    @Test
    public void hitServiceWhenValidRequests() {
        ImportRequest[] rs = newImportRequests(2);
        target.submit(rs);
        verifyServiceCalled(1);
    }

    private static void assertResponse(Either<Error, ImportResponse[]> actual,
                                       int howManyExpected) {
        assertNotNull(actual);
        assertTrue(actual.isRight());

        ImportResponse[] response = actual.getRight();
        assertNotNull(response);
        assertThat(response.length, is(howManyExpected));
        assertFalse(hasNulls(response));
    }

    @Test
    public void emptyResponseIfNullRequests() {
        assertResponse(target.submit((ImportRequest[]) null), 0);
    }

    @Test
    public void emptyResponseIfRequestsAllNull() {
        assertResponse(target.submit(null, null, null), 0);
    }

    @Test
    public void filterOutNullRequests() {
        ImportRequest[] rs = newImportRequests(2);
        assertResponse(target.submit(rs[0], null, rs[1]), 2);
    }

    @Test
    public void errorOutIfSomeRequestsFailValidation() {
        ImportRequest[] rs = newImportRequests(2);
        rs[1].sessionKey = null;

        Either<Error, ImportResponse[]> actual = target.submit(rs);

        assertNotNull(actual);
        assertTrue(actual.isLeft());
        assertNotNull(actual.getLeft());
    }

}

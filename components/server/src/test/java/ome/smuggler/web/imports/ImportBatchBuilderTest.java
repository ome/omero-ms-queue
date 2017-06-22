package ome.smuggler.web.imports;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static java.util.stream.Collectors.toSet;
import static ome.smuggler.web.imports.Utils.*;
import static util.string.Strings.*;

import ome.smuggler.core.types.ImportInput;
import ome.smuggler.web.Error;
import org.junit.Before;
import org.junit.Test;
import util.object.Either;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class ImportBatchBuilderTest {

    private static void assertEmptyStream(
            Either<Error, List<ImportInput>> actual) {
        assertNotNull(actual);
        assertTrue(actual.isRight());
        assertThat(actual.getRight().size(), is(0));
    }

    private static void assertStream(Either<Error, List<ImportInput>> actual,
                                     ImportRequest...expected) {
        assertNotNull(actual);
        assertTrue(actual.isRight());

        Set<String> actualSessions = actual.getRight()
                                           .stream()
                                           .map(ImportInput::getSessionKey)
                                           .map(Object::toString)
                                           .collect(toSet());
        Set<String> expectedSessions = Stream.of(expected)
                                         .map(r -> r.sessionKey)
                                         .collect(toSet());
        assertThat(actualSessions, is(expectedSessions));
    }

    private static void assertValidationError(
            Either<Error, List<ImportInput>> actual,
            long expectedLinesCount) {
        assertNotNull(actual);
        assertTrue(actual.isLeft());

        Error e = actual.getLeft();
        assertNotNull(e);
        assertFalse(isNullOrEmpty(e.reason));
        assertThat(lines(e.reason).filter(x -> !isNullOrEmpty(x)).count(),
                   is(expectedLinesCount));
    }

    private ImportBatchBuilder target;

    @Before
    public void setup() {
        target = new ImportBatchBuilder();
    }

    @Test
    public void buildEmptyStreamIfNullRequests() {
        assertEmptyStream(target.build((ImportRequest[]) null));
    }

    @Test
    public void buildEmptyStreamIfRequestsAllNull() {
        assertEmptyStream(target.build(null, null));
    }

    @Test
    public void buildFromOneRequest() {
        ImportRequest r = newImportRequest(1);
        assertStream(target.build(r), r);
    }

    @Test
    public void buildFromManyRequests() {
        ImportRequest[] rs = newImportRequests(2);
        assertStream(target.build(rs), rs);
    }

    @Test
    public void buildFromManyRequestsWithSomeNull() {
        ImportRequest[] rs = newImportRequests(2);
        assertStream(target.build(null, rs[0], null, rs[1]), rs);
    }

    @Test
    public void buildFromManyRequestsWithSomeInvalid() {
        ImportRequest[] rs = newImportRequests(3);
        rs[1].sessionKey = null;
        assertValidationError(target.build(rs), 1);
    }

    @Test
    public void buildFromManyRequestsWithSomeInvalidAndSomeNull() {
        ImportRequest[] rs = newImportRequests(3);
        rs[0].sessionKey = null;
        rs[1].sessionKey = null;
        assertValidationError(target.build(rs[0], null, rs[1], rs[2], null), 2);
    }

}

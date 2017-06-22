package util.validation;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.validation.ParserFactory.filePathUriParser;

import java.net.URI;
import java.nio.file.Paths;

import org.junit.Test;

import util.object.Either;


public class FilePathUriParser {

    private static boolean runningOnWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }

    private static URI assertHasBuiltFileUri(String rawValue) {
        Either<String, URI> actual = filePathUriParser().parse(rawValue);
        assertTrue(actual.getLeft(), actual.isRight());
        
        URI parsed = actual.getRight();
        assertNotNull(parsed);
        assertThat(parsed.getScheme(), is("file"));
        
        return parsed;
    }

    @Test
    public void relativePath() {        
        String rawRelPath = runningOnWindows() ? "a b\\c"
                                               : "a b/c";
        URI actual = assertHasBuiltFileUri(rawRelPath);
        String absPath = Paths.get(actual).toString();
        assertTrue(absPath, absPath.endsWith(rawRelPath));
    }

    @Test
    public void absolutePath() {        
        String expectedRawAbsPath = runningOnWindows() ? "C:\\a b\\c"
                                                       : "/a b/c";
        URI actual = assertHasBuiltFileUri(expectedRawAbsPath);
        String actualRawAbsPath = Paths.get(actual).toString();
        assertThat(actualRawAbsPath, is(expectedRawAbsPath));
    }

}

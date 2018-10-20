package end2end.artemis;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.rules.ExternalResource;

/**
 * Adapted from:
 * - https://www.dontpanicblog.co.uk/2017/05/12/test-system-out-with-junit/
 */
public class SystemOutRule extends ExternalResource {

    private PrintStream originalStdout;
    private PrintStream originalStderr;
    private final ByteArrayOutputStream outputBuffer =
            new ByteArrayOutputStream();

    @Override
    protected void before() throws Throwable {
        originalStdout = System.out;
        originalStderr = System.err;

        PrintStream outStream = new PrintStream(outputBuffer);
        System.setOut(outStream);
        System.setErr(outStream);
    }

    @Override
    protected void after() {
        System.setOut(originalStdout);
        System.setErr(originalStderr);
    }

    public String asString() {
        return outputBuffer.toString();
    }

}

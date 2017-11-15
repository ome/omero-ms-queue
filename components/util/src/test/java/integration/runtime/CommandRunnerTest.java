package integration.runtime;

import static org.junit.Assert.*;
import static util.object.Pair.pair;

import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.Test;
import util.io.StreamOps;
import util.object.Pair;
import util.runtime.BaseProgramArgument;
import util.runtime.CommandBuilder;
import util.runtime.CommandRunner;
import util.runtime.ProgramArgument;
import util.runtime.jvm.*;
import util.sequence.Arrayz;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class CommandRunnerTest {

    private static ClassPath classPath() {
        Optional<Path> basePath = ClassPathLocator.findBase(LinesProducer.class);
        return new ClassPath().add(basePath.get());
    }

    private static CommandBuilder command(boolean delayWrites) {
        ProgramArgument<String> mainClass =
                new BaseProgramArgument<>(LinesProducer.class.getName());
        ClassPathJvmArg classPath = new ClassPathJvmArg(classPath());
        ProgramArgument<String> maybeDelay =
                new BaseProgramArgument<>(
                        delayWrites ? LinesProducer.RequestWriteDelay
                                    : "");

        return JvmCmdFactory.java(classPath, mainClass)
                            .addApplicationArgument(maybeDelay);
    }

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    private Pair<Integer, String[]> execLinesProducerAndReadOutputFile(
            Path output)
            throws Exception {
        CommandRunner runner = new CommandRunner(command(false));

        int exitCode = runner.exec(output);
        String[] outputFileLines = Files.lines(output)
                                        .toArray(String[]::new);

        return pair(exitCode, outputFileLines);
    }

    private Pair<Integer, String[]> execLinesProducerAndReadStreamedOutput()
            throws Exception {
        Function<InputStream, String[]> outputReader =
                in -> StreamOps.readLines(in, xs -> xs.toArray(String[]::new));
        CommandRunner runner = new CommandRunner(command(true));

        return runner.exec(outputReader);
    }

    private void assertExecOutcome(Pair<Integer, String[]> result) {
        assertEquals(new Integer(0), result.fst());
        assertArrayEquals(LinesProducer.lines, result.snd());
    }

    @Test
    public void canReadAllStreamedOutput() throws Exception {
        Pair<Integer, String[]> result =
                execLinesProducerAndReadStreamedOutput();
        assertExecOutcome(result);
    }

    @Test
    public void canReadOutputFile() throws Exception {
        Path output = tempFolder.newFile().toPath();
        Pair<Integer, String[]> result =
                execLinesProducerAndReadOutputFile(output);
        assertExecOutcome(result);
    }

    @Test
    public void appendToOutputFileIfItExists() throws Exception {
        Path output = tempFolder.newFile().toPath();
        Pair<Integer, String[]> fstRun =
                execLinesProducerAndReadOutputFile(output);
        Pair<Integer, String[]> sndRun =
                execLinesProducerAndReadOutputFile(output);
        String[] expectedLines =
                Arrayz.op(String[]::new)
                      .concat(LinesProducer.lines, LinesProducer.lines);

        assertEquals(new Integer(0), fstRun.fst());
        assertEquals(new Integer(0), sndRun.fst());

        assertArrayEquals(expectedLines, sndRun.snd());
    }

    @Test
    public void createOutputFileIfItDoesntExist() throws Exception {
        Path output = Paths.get(tempFolder.getRoot().getAbsolutePath(),
                                UUID.randomUUID().toString());
        assertFalse(Files.exists(output));

        Pair<Integer, String[]> result =
                execLinesProducerAndReadOutputFile(output);
        assertExecOutcome(result);
    }

}

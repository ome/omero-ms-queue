package util.runtime;

import static org.junit.Assert.*;

import util.io.StreamOps;
import org.junit.Test;
import util.object.Pair;
import util.runtime.jvm.*;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;

public class CommandRunnerTest {

    private static ClassPath classPath() {
        Optional<Path> basePath = ClassPathLocator.findBase(LinesProducer.class);
        return new ClassPath().add(basePath.get());
    }

    private static CommandBuilder command() {
        ProgramArgument<String> mainClass =
                new BaseProgramArgument<>(LinesProducer.class.getName());
        ClassPathJvmArg classPath = new ClassPathJvmArg(classPath());

        return JvmCmdFactory.java(classPath, mainClass);
    }

    private static Pair<Integer, String[]> callLinesProducer() throws Exception {
        Function<InputStream, String[]> outputReader =
                in -> StreamOps.readLines(in, xs -> xs.toArray(String[]::new));
        CommandRunner runner = new CommandRunner(command());

        return runner.exec(outputReader);
    }


    @Test
    public void canReadAllOutput() throws Exception {
        Pair<Integer, String[]> actual = callLinesProducer();

        assertEquals(new Integer(0), actual.fst());
        assertArrayEquals(LinesProducer.lines, actual.snd());
    }

}

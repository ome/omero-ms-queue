package util.runtime;

import static org.junit.Assert.*;
import static util.sequence.Arrayz.asList;
import static util.sequence.Arrayz.array;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class CommandLineBuilderTest {

    @DataPoints
    public static final String[][] tokensSupply = new String[][] {
        array(), array("1"), array("1", "2"), array("1", "2", "3")
    };
    
    private static CommandLineBuilder newBuilder(
            ProgramArgument<Path> programPath, 
            Stream<CommandBuilder> args) {
        return new CommandLineBuilder(programPath) {
            @Override
            protected Stream<CommandBuilder> arguments() {
                return args;
            }
        };
    }
    
    private static ProgramArgument<Path> prog(String name) {
        return new BaseProgramArgument<>(Paths.get(name));
    }
    
    private static CommandBuilder arg(String...tokens) {
        return new ListProgramArgument<>(asList(tokens));
    }
    
    private static Stream<CommandBuilder> buildArgs(String[]...xs) {
        return Stream.of(xs).map(CommandLineBuilderTest::arg);
    }
    
    public static String[] concat(String[]...xs) {
        return Stream.of(xs)
                     .map(Stream::of)
                     .flatMap(x -> x)
                     .toArray(String[]::new);
    }
        
    @Theory
    public void tokensPreservesOrderInWhichArgsAreGiven(
            String[] xs, String[] ys, String[] zs) {
        String progName = "prog";
        Stream<CommandBuilder> args = buildArgs(xs, ys, zs);
        CommandBuilder target = newBuilder(prog(progName), args);
        
        String[] actual = target.tokens().toArray(String[]::new);
        String[] expected = concat(new String[] { progName }, xs, ys, zs);
        assertArrayEquals(expected, actual);
    }
    
    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullArg() {
        newBuilder(null, Stream.empty());
    }
    
    @Test (expected = NullPointerException.class)
    public void tokensThrowsIfArgumentsReturnsNull() {
        newBuilder(prog(""), null).tokens();
    }

    @Test (expected = NullPointerException.class)
    public void tokensThrowsIfArgumentsReturnsStreamWithNulls() {
        Stream<CommandBuilder> args = Stream.of(arg(), null);
        newBuilder(prog(""), args).tokens().toArray();
    }
    
}

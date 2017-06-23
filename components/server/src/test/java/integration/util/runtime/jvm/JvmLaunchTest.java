package integration.util.runtime.jvm;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static java.util.stream.Collectors.toList;
import static util.sequence.Arrayz.array;
import static util.sequence.Arrayz.asStream;
import static util.sequence.Streams.intersperse;
import static util.string.Strings.readAsString;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import util.object.Pair;
import util.runtime.BaseProgramArgument;
import util.runtime.CommandBuilder;
import util.runtime.ProgramArgument;
import util.runtime.jvm.ClassPath;
import util.runtime.jvm.ClassPathJvmArg;
import util.runtime.jvm.ClassPathLocator;
import util.runtime.jvm.JvmCmdFactory;
import util.runtime.jvm.SysPropJvmArg;
import util.sequence.Arrayz;

public class JvmLaunchTest {

    private static ClassPath classPath() {
        Optional<Path> basePath = ClassPathLocator.findBase(CmdCheck.class); 
        return new ClassPath().add(Paths.get("non existant")) // (*)
                              .add(basePath.get());
    }
    // (*) testing what happens with a space; if not properly quoted the actual
    // base path that follows is useless and the launch will bomb out.
    
    private static Pair<String, String>[] sysProps() {
        return array(new Pair<>(" some key ", " some value "),  // (*)
                     new Pair<>("k2", "v2")
                );
    }
    // (*) should be able to set props whose keys or values have spaces; e.g.
    // "-D some key = some value " should result in k=' some key ', v=' some value '
    
    private static String expectedReceivedAppArgs() {
        Stream<String> xs = asStream(sysProps())
                           .map(p -> Stream.of(p.fst(), p.snd()))
                           .flatMap(x -> x);
        String interspersedWithSeparator = intersperse(
                                                () -> CmdCheck.ArgSeparator, xs)
                                          .collect(Collectors.joining(""));
        return interspersedWithSeparator + CmdCheck.ArgSeparator; 
    }
    
    private static 
    Stream<ProgramArgument<String>> keyValueArgs(Pair<String, String> p) {
        String key = p.fst(), value = p.snd();
        return Stream.of(key, value).map(BaseProgramArgument<String>::new);
    }
    
    private static CommandBuilder command() {
        SysPropJvmArg[] props = Arrayz.op(SysPropJvmArg[]::new)
                                      .map((ix, p) -> new SysPropJvmArg(p), 
                                           sysProps());
        @SuppressWarnings("unchecked")
        ProgramArgument<String>[] appArgs = Stream
                                           .of(sysProps())
                                           .map(JvmLaunchTest::keyValueArgs)
                                           .flatMap(s -> s)
                                           .toArray(ProgramArgument[]::new);
        ProgramArgument<String> mainClass = 
                new BaseProgramArgument<>(CmdCheck.class.getName());
        ClassPathJvmArg classPath = new ClassPathJvmArg(classPath());
        
        return JvmCmdFactory.java(classPath, mainClass)
                            .addProp(props)
                            .addApplicationArgument(appArgs);
    }
    
    private static Process startCmdCheckProcess() throws Exception {
        List<String> commandLine = command().tokens().collect(toList());
        ProcessBuilder builder = new ProcessBuilder(commandLine);
        Process cmdCheck = builder.start();
        cmdCheck.getOutputStream().close();
        
        return cmdCheck;
    }
    
    @Test
    public void fullyFledgedMainClassLaunch() throws Exception {
        Process cmdCheck = startCmdCheckProcess();
        String receivedClassPath = readAsString(cmdCheck.getErrorStream());
        String receivedAppArgs = readAsString(cmdCheck.getInputStream());
        
        int status = cmdCheck.waitFor();
        
        assertThat(status, is(0));
        assertThat(receivedClassPath, is(classPath().toString()));
        assertThat(receivedAppArgs, is(expectedReceivedAppArgs()));
    }
    
}

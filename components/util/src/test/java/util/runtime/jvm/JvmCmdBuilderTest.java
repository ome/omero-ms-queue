package util.runtime.jvm;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.sequence.Arrayz.array;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.Test;

import util.runtime.BaseProgramArgument;
import util.runtime.ProgramArgument;

public class JvmCmdBuilderTest {

    @Test
    public void firstTokenIsJava() {
        Path jarFile = Paths.get("");
        Optional<String> java = JvmCmdFactory.java(new JarJvmArg(jarFile))
                                             .tokens()
                                             .findFirst();
        assertTrue(java.isPresent());
        assertThat(java.get(), endsWith(JvmName.find().toString()));
        
        ProgramArgument<Path> jvm = new BaseProgramArgument<>(Paths.get("java"));
        java = JvmCmdFactory.java(jvm, new JarJvmArg(jarFile))
                            .tokens()
                            .findFirst();
        
        assertTrue(java.isPresent());
        assertThat(java.get(), is("java"));
    }
    
    @Test
    public void fullyFledgedJarCommandLine() {
        String[] actual = JvmCmdFactory
                .java(new JarJvmArg(Paths.get("my.jar")))
                .addProp(new SysPropJvmArg("k1", "v1"))
                .addProp(new SysPropJvmArg("k2", "v2"))
                .addApplicationArgument(new BaseProgramArgument<>("a1"))
                .addApplicationArgument(new BaseProgramArgument<>("a2"))
                .tokens()
                .skip(1)  // get rid of command path
                .toArray(String[]::new);
        
        String[] expected = array("-jar", "my.jar", 
                                  "-Dk1=v1", "-Dk2=v2", 
                                  "a1", "a2");
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void fullyFledgedMainClassCommandLine() {
        ClassPathJvmArg classPath = new ClassPathJvmArg(
                                    new ClassPath().add(Paths.get("my.jar")));
        ProgramArgument<String> mainClass = new BaseProgramArgument<>("Main");
        String[] actual = JvmCmdFactory
                .java(classPath, mainClass)
                .addProp(new SysPropJvmArg("k1", "v1"))
                .addProp(new SysPropJvmArg("k2", "v2"))
                .addApplicationArgument(new BaseProgramArgument<>("a1"))
                .addApplicationArgument(new BaseProgramArgument<>("a2"))
                .tokens()
                .skip(1)  // get rid of command path
                .toArray(String[]::new);
        
        String[] expected = array("-cp", "my.jar", 
                                  "-Dk1=v1", "-Dk2=v2",
                                  "Main",
                                  "a1", "a2");
        
        assertArrayEquals(expected, actual);
    }
    
}

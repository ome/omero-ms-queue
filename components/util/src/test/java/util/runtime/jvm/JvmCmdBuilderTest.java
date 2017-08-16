package util.runtime.jvm;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.sequence.Arrayz.array;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.Test;

import util.runtime.BaseProgramArgument;
import util.runtime.CommandBuilder;
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

    @Test
    public void canAddSysProps() {
        String key = "file.separator";
        String fileSep = System.getProperty(key);
        assertNotNull(fileSep);  // should always be there!

        Path jarFile = Paths.get("");
        JvmCmdBuilder target = JvmCmdFactory.java(new JarJvmArg(jarFile))
                                            .addCurrentSysProps();

        Optional<String> actual = target.tokens()
                                        .filter(t -> t.contains(key))
                                        .findFirst();
        assertTrue(actual.isPresent());
    }

    @Test (expected = NullPointerException.class)
    public void addPropThrowsIfNullArray() {
        Path jarFile = Paths.get("");
        JvmCmdFactory.java(new JarJvmArg(jarFile))
                     .addProp((SysPropJvmArg[])null);
    }

    @Test (expected = NullPointerException.class)
    public void addPropThrowsIfArrayContainsNulls() {
        Path jarFile = Paths.get("");
        JvmCmdFactory.java(new JarJvmArg(jarFile))
                     .addProp(new SysPropJvmArg("k", "v"), null);
    }

    @Test (expected = NullPointerException.class)
    public void addApplicationArgumentThrowsIfNullArray() {
        Path jarFile = Paths.get("");
        JvmCmdFactory.java(new JarJvmArg(jarFile))
                     .addApplicationArgument((CommandBuilder[])null);
    }

    @Test (expected = NullPointerException.class)
    public void addApplicationArgumentThrowsIfArrayContainsNulls() {
        Path jarFile = Paths.get("");
        JvmCmdFactory.java(new JarJvmArg(jarFile))
                     .addApplicationArgument(
                             new BaseProgramArgument<>(), null);
    }

}

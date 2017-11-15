package util.runtime.jvm;

import static org.junit.Assert.*;

import org.junit.Test;
import util.runtime.ProgramArgument;

import java.nio.file.Path;

public class JvmCmdFactoryTest {

    @Test
    public void findThisJvm() {
        ProgramArgument<Path> target = JvmCmdFactory.thisJvm();
        assertNotNull(target);
    }

    @Test
    public void ctor() {
        new JvmCmdFactory();  // only to get 100% coverage.
    }

}

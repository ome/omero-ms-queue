package integration.config;

import static org.junit.Assert.*;
import static util.spring.io.ResourceLocation.classpath;
import static util.spring.io.ResourceLocation.filepathFromCwd;

import java.io.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import util.config.ConfigProvider;
import util.spring.io.ResourceLocation;
import ome.smuggler.config.providers.PriorityConfigProvider;
import ome.smuggler.run.RunnableApp;


public abstract class ConfigFileTest<T> {

    private static void writeStdoutToFile(File f, Runnable outWriter)
            throws IOException {
        FileOutputStream fileContents = new FileOutputStream(f);
        PrintStream ps = new PrintStream(fileContents);
        PrintStream currentStdout = System.out;
        try {
            System.setOut(ps);
            outWriter.run();
            System.out.flush();
        } finally {
            System.setOut(currentStdout);
        }
    }


    @Rule
    public final TemporaryFolder configDirUnderPwd = new TemporaryFolder(new File("./"));
    
    protected abstract PriorityConfigProvider<T> getConfigProvider();
    
    protected abstract RunnableApp getFileGenerator();
    
    protected abstract ConfigProvider<T> getFileContents();

    private ResourceLocation writeFile() throws IOException {
        String fileName = getConfigProvider().getConfigFileName();
        File configFile = configDirUnderPwd.newFile(fileName);

        writeStdoutToFile(configFile, () -> getFileGenerator().run(null));

        String configDirName = configDirUnderPwd.getRoot().getName();
        return filepathFromCwd(configDirName, fileName);
    }
    
    @Test
    public void readConfigFileFromConfigDirUnderPwd() throws Exception {
        ResourceLocation pathRelativeToPwd = writeFile();
        
        Object[] actual = getConfigProvider()
                         .readConfig(pathRelativeToPwd)
                         .toArray();
        Object[] expected = getFileContents().readConfig().toArray();
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void defaultToHardCodedConfigIfNoOtherAvailable() throws Exception {
        
        Object[] actual = getConfigProvider()
                         .readConfig(classpath("some"), classpath("nonsense"))
                         .toArray();
        Object[] expected = getConfigProvider().getFallback().toArray();
        
        assertArrayEquals(expected, actual);
    }
    
}

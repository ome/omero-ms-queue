package util.config;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.junit.Test;

public class ConfigReaderTest {

    private static ConfigProvider<URI> newReader(String sourceValue) {
        SingleStringItemConfigProvider source = 
                new SingleStringItemConfigProvider(() -> sourceValue);
        return StringConfigReaderFactory.makeURI(source);
    }
    
    @Test (expected = NullPointerException.class)
    public void newReaderThrowsIfNullFirstArg() {
        ConfigReader.newReader(null, x -> x);
    }
    
    @Test (expected = NullPointerException.class)
    public void newReaderThrowsIfNullSecondArg() {
        ConfigReader.newReader(newReader("x"), null);
    }
    
    @Test (expected = URISyntaxException.class)
    public void rethrowExceptionAsIsIfMappingFromStringThrows() {
        newReader("  ").defaultReadConfig().findFirst();
    }
    
    @Test
    public void mapUrn() {
        String sourceUrn = "urn:1";
        Optional<URI> mappedUrn = newReader(sourceUrn)
                                 .defaultReadConfig()
                                 .findFirst();
        
        assertNotNull(mappedUrn);
        assertTrue(mappedUrn.isPresent());
        assertEquals(sourceUrn, mappedUrn.get().toASCIIString());
    }
    
}

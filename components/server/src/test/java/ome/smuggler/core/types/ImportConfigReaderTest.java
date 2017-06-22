package ome.smuggler.core.types;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Test;

import ome.smuggler.config.data.ImportYmlFile;
import ome.smuggler.config.items.ImportConfig;

public class ImportConfigReaderTest {

    private static ImportConfig validConfig() {
        return new ImportYmlFile().defaultReadConfig().findFirst().get();
    }
    
    private static ImportConfigReader reader(Consumer<ImportConfig> tweak) {
        ImportConfig cfg = validConfig();
        tweak.accept(cfg);
        return new ImportConfigReader(cfg);
    }
    
    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullArg() {
        new ImportConfigReader(null);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void rejectNullLogDir() {
        reader(cfg -> cfg.setImportLogDir(null));
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void rejectEmptyLogDir() {
        reader(cfg -> cfg.setImportLogDir(""));
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void rejectNullFailedLogDir() {
        reader(cfg -> cfg.setFailedImportLogDir(null));
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void rejectEmptyFailedLogDir() {
        reader(cfg -> cfg.setFailedImportLogDir(""));
    }
 
    @Test (expected = NullPointerException.class)
    public void rejectNullRetentionPeriod() {
        reader(cfg -> cfg.setLogRetentionMinutes(null));
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void rejectNegativeRetentionPeriod() {
        reader(cfg -> cfg.setLogRetentionMinutes(-1L));
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void rejectZeroRetentionPeriod() {
        reader(cfg -> cfg.setLogRetentionMinutes(0L));
    }
    
    @Test
    public void acceptNullRetryIntervals() {
        List<Duration> actual = reader(cfg -> cfg.setRetryIntervals(null))
                               .retryIntervals();
        assertNotNull(actual);
        assertThat(actual.size(), is(0));
    }
    
    @Test
    public void acceptEmptyRetryIntervals() {
        List<Duration> actual = reader(cfg -> cfg.setRetryIntervals(new Long[0]))
                               .retryIntervals();
        assertNotNull(actual);
        assertThat(actual.size(), is(0));
    }

}

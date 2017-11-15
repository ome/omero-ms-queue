package kew.providers.artemis.config;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static kew.providers.artemis.config.StorageProps.*;

import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.server.JournalType;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;


public class StoragePropsTest {

    private static void assertHasStorageDirs(Configuration cfg,
                                             String dataDirName) {
        assertThat(cfg.getBindingsDirectory(), containsString(dataDirName));
        assertThat(cfg.getJournalDirectory(), containsString(dataDirName));
        assertThat(cfg.getLargeMessagesDirectory(), containsString(dataDirName));
        assertThat(cfg.getPagingDirectory(), containsString(dataDirName));
    }

    @Test
    public void enablePersistence() {
        Configuration actual = CoreConfigFactory.empty()
                                                .with(persistenceEnabled(true))
                                                .apply(null);
        assertTrue(actual.isPersistenceEnabled());
    }

    @Test
    public void disablePersistence() {
        Configuration actual = CoreConfigFactory.empty()
                                                .with(persistenceEnabled(false))
                                                .apply(null);
        assertFalse(actual.isPersistenceEnabled());
    }

    @Test
    public void storageOnSetsDirectories() {
        String dataDirName = "artemis-data";
        Path dataDir = Paths.get(dataDirName);
        Configuration actual = CoreConfigFactory.empty()
                                                .with(storageOn(dataDir))
                                                .apply(null);
        assertHasStorageDirs(actual,dataDirName);
    }

    @Test
    public void persistenceEnablesAndSetsJournalType() {
        JournalType journalType = JournalType.MAPPED;
        Configuration actual = CoreConfigFactory.empty()
                                                .with(persistence(journalType))
                                                .apply(null);
        assertTrue(actual.isPersistenceEnabled());
        assertThat(actual.getJournalType(), is(journalType));
    }

    @Test
    public void defaultSettingsHaveNio() {
        Path dataDir = Paths.get("artemis-data");
        Configuration actual =
                CoreConfigFactory.empty()
                                 .with(defaultStorageSettings(dataDir))
                                 .apply(null);
        assertThat(actual.getJournalType(), is(JournalType.NIO));
    }

    @Test (expected = NullPointerException.class)
    public void defaultStorageSettingsThrowsIfNullDataDir() {
        defaultStorageSettings(null);
    }

    @Test (expected = NullPointerException.class)
    public void persistenceThrowsIfNullJournalType() {
        persistence(null);
    }

    @Test (expected = NullPointerException.class)
    public void storageOnThrowsIfNullDataDir() {
        storageOn(null);
    }

    @Test
    public void ctor() {
        new StorageProps();  // only to get 100% coverage.
    }

}

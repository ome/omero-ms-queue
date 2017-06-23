package ome.smuggler.core.types;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static ome.smuggler.core.io.crypto.CryptoKeyFactory.exportKey;

import ome.smuggler.config.data.CryptoYmlFile;
import ome.smuggler.config.items.CryptoConfig;
import org.junit.Test;

import java.util.Optional;
import java.util.function.Consumer;

public class CryptoConfigReaderTest {

    private static CryptoConfig validConfig() {
        return new CryptoYmlFile().first();
    }

    private static CryptoConfigSource reader(Consumer<CryptoConfig> tweak) {
        CryptoConfig cfg = validConfig();
        tweak.accept(cfg);
        return new CryptoConfigReader(cfg);
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullArg() {
        new CryptoConfigReader(null);
    }

    @Test
    public void acceptNullEncryptField() {
        CryptoConfigSource target = reader(cfg -> cfg.setEncrypt(null));
        assertFalse(target.encrypt());
    }

    @Test
    public void ignoreKeyIfEncryptFieldFalse() {
        CryptoConfigSource target = reader(cfg -> cfg.setEncrypt(false));
        assertFalse(target.encrypt());
        assertThat(target.key(), is(Optional.empty()));
    }

    @Test (expected = IllegalArgumentException.class)
    public void rejectNullKeyFieldIfEncryptFieldTrue() {
        CryptoConfigSource target = reader(cfg -> {
            cfg.setEncrypt(true);
            cfg.setKey(null);
        });
    }

    @Test
    public void validConfigWithEncryptionOn() {
        CryptoConfigSource target = reader(cfg -> cfg.setEncrypt(true));

        assertNotNull(target.key());
        assertTrue(target.key().isPresent());

        String reExported = exportKey(target.key().get());
        assertThat(reExported, is(validConfig().getKey()));
    }

}

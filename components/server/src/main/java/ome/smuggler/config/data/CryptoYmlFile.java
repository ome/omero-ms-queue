package ome.smuggler.config.data;

import java.util.stream.Stream;

import ome.smuggler.config.items.CryptoConfig;
import util.io.crypto.CryptoAlgoSpec;
import util.io.crypto.CryptoKeyFactory;
import util.config.ConfigProvider;

/**
 * Default configuration for encryption of sensitive data, i.e. the content
 * of the YAML file if not explicitly provided.
 */
public class CryptoYmlFile implements ConfigProvider<CryptoConfig> {

    private static final String encryptionKey =
            CryptoKeyFactory.exportNewKey(CryptoAlgoSpec.AES);

    @Override
    public Stream<CryptoConfig> readConfig() {
        CryptoConfig cfg = new CryptoConfig();
        cfg.setEncrypt(false);
        cfg.setKey(encryptionKey);  // (*)

        return Stream.of(cfg);
    }
    /* (*) even if encryption is disabled we generate the key out of
     * convenience so that it can be used later if encryption is turned on.
     */
}

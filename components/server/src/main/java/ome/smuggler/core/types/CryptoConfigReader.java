package ome.smuggler.core.types;

import static java.util.Objects.requireNonNull;
import static util.io.crypto.CryptoKeyFactory.importKey;

import ome.smuggler.config.items.CryptoConfig;

import java.security.Key;
import java.util.Optional;

/**
 * Implements {@link CryptoConfigSource} by extracting and validating values
 * from an underlying {@link CryptoConfig}.
 */
public class CryptoConfigReader implements CryptoConfigSource {

    private final boolean encrypt;
    private final Optional<Key> maybeKey;

    /**
     * Creates a new instance.
     * @param config the configuration as obtained by the configuration
     * provider.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public CryptoConfigReader(CryptoConfig config) {
        requireNonNull(config, "config");

        if (config.getEncrypt() == null || !config.getEncrypt()) {
            encrypt = false;
            maybeKey = Optional.empty();
        } else {
            encrypt = true;
            Key key = importKey(config.getKey());
            maybeKey = Optional.of(key);
        }
    }

    @Override
    public boolean encrypt() {
        return encrypt;
    }

    @Override
    public Optional<Key> key() {
        return maybeKey;
    }

}

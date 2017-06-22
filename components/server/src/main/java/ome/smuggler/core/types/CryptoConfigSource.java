package ome.smuggler.core.types;

import ome.smuggler.config.items.CryptoConfig;

import java.security.Key;
import java.util.Optional;

/**
 * Provides read-only, type-safe access to the crypto configuration.
 * @see CryptoConfig
 */
public interface CryptoConfigSource {

    /**
     * @return whether to encrypt sensitive data.
     */
    boolean encrypt();

    /**
     * @return the encryption key to use if encryption is {@link #encrypt()
     * turned on}.
     */
    Optional<Key> key();

}

package ome.smuggler.config.data;

import ome.smuggler.core.types.CryptoConfigSource;

import java.security.Key;
import java.util.Optional;

/**
 * Dev crypto settings.
 */
public class DevCryptoConfigSource implements CryptoConfigSource {

    @Override
    public boolean encrypt() {
        return false;
    }

    @Override
    public Optional<Key> key() {
        return Optional.empty();
    }

}

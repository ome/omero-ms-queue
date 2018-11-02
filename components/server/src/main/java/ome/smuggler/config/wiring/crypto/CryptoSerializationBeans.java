package ome.smuggler.config.wiring.crypto;

import java.security.Key;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ome.smuggler.core.types.CryptoConfigSource;
import util.io.crypto.CipherFactory;
import util.io.crypto.CryptoAlgoSpec;
import util.serialization.CryptoSerializationFactory;
import util.serialization.SerializationFactory;
import util.serialization.json.JsonSerializationFactory;

/**
 * Spring bean wiring configuration for serialization.
 * Builds serializers depending on the current crypto configuration.
 * If encryption is turned off, then we use plain JSON serialization. On the
 * other hand, when encryption is turned on, the output of JSON serialization
 * is encrypted and when de-serializing, the input is first decrypted and then
 * de-serialized from JSON.
 * Services that read/write sensitive data (e.g. session keys) to disk
 * (directly or indirectly through the queue) use these provided factory
 * methods to make sure sensitive data is protected.
 */
@Configuration
public class CryptoSerializationBeans {

    private SerializationFactory plainSerialization() {
        return new JsonSerializationFactory();
    }

    private SerializationFactory cryptoSerialization(Key key) {
        CryptoAlgoSpec algo = CryptoAlgoSpec.AES;
        CipherFactory crypto = new CipherFactory(algo, key);
        return new CryptoSerializationFactory(crypto, plainSerialization());
    }

    @Bean
    public SerializationFactory serializationFactory(CryptoConfigSource cfg) {
        return cfg.key()
                  .map(this::cryptoSerialization)
                  .orElseGet(this::plainSerialization);
    }

}

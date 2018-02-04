package util.serialization.yaml;

import org.junit.Before;
import util.io.crypto.CipherFactory;
import util.io.crypto.CryptoAlgoSpec;
import util.io.crypto.CryptoKeyFactory;
import util.serialization.CryptoSerializationFactory;
import util.serialization.SerializationFactory;

import java.security.Key;


public class CryptoSerializationFactoryTest
        extends YamlSerializationFactoryTest {

    private static CipherFactory crypto() {
        CryptoAlgoSpec algo = CryptoAlgoSpec.AES;
        Key key = CryptoKeyFactory.generateKey(algo);
        return new CipherFactory(algo, key);
    }


    private SerializationFactory factory;

    @Before
    public void setup() {
        factory = new CryptoSerializationFactory(
                        crypto(), new YamlSerializationFactory());
    }

    @Override
    protected SerializationFactory factory() {
        return factory;
    }

}

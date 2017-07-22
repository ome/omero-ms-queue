package util.io.crypto;

import static org.mockito.Mockito.mock;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import javax.crypto.Cipher;
import java.security.Key;
import java.util.function.Function;

@RunWith(Theories.class)
public class CipherFactoryTest {

    @DataPoints
    public static CryptoAlgoSpec[] algos = CryptoAlgoSpec.values();

    public void instantiateFreshCipher(CryptoAlgoSpec algo,
                                       Function<CipherFactory, Cipher> f) {
        Key key = CryptoKeyFactory.generateKey(algo);
        CipherFactory target = new CipherFactory(algo, key);

        Cipher c1 = f.apply(target);
        assertNotNull(c1);

        Cipher c2 = f.apply(target);
        assertNotNull(c2);

        assertFalse(c1 == c2);
    }

    @Theory
    public void instantiateFreshEncryptionCipher(CryptoAlgoSpec algo) {
        instantiateFreshCipher(algo, CipherFactory::encryptionChipher);
    }

    @Theory
    public void instantiateFreshDecryptionCipher(CryptoAlgoSpec algo) {
        instantiateFreshCipher(algo, CipherFactory::decryptionChipher);
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullAlgo() {
        new CipherFactory(null, mock(Key.class));
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullKey() {
        new CipherFactory(CryptoAlgoSpec.AES, null);
    }

}

package ome.smuggler.core.io.crypto;

import static java.util.Objects.requireNonNull;
import static util.error.Exceptions.unchecked;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 * Factory to instantiate fresh {@link Cipher}s for a given algorithm and key.
 */
public class CipherFactory {

    private final CryptoAlgoSpec algo;
    private final Key key;

    /**
     * Creates a new instance.
     * @param algo the JCA algorithm to use.
     * @param key a key compatible with the algorithm.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public CipherFactory(CryptoAlgoSpec algo, Key key) {
        requireNonNull(algo, "algo");
        requireNonNull(key, "key");

        this.algo = algo;
        this.key = key;
    }

    private Cipher newCipher(int mode)
            throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException {
        Cipher c = Cipher.getInstance(algo.spec());
        c.init(mode, key);
        return c;
    }

    /**
     * @return a fresh cipher initialised for encryption.
     */
    public Cipher encryptionChipher() {
        return unchecked(() -> newCipher(Cipher.ENCRYPT_MODE)).get();
    }

    /**
     * @return a fresh cipher initialised for decryption.
     */
    public Cipher decryptionChipher() {
        return unchecked(() -> newCipher(Cipher.DECRYPT_MODE)).get();
    }

}

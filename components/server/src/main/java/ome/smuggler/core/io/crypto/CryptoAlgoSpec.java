package ome.smuggler.core.io.crypto;

/**
 * Enumerates the JCA algorithms we use.
 */
public enum CryptoAlgoSpec {

    AES,
    AES_ECB_PKCS5Padding;  // NB '_' maps to '/', see spec() below.

    /**
     * @return the algorithm string formatted as "name/mode/padding scheme".
     */
    public String spec() {
        return name().replace('_', '/');
    }

    /**
     * @return the algorithm name.
     */
    public String canonicalName() {
        return name().split("_")[0];
    }

}

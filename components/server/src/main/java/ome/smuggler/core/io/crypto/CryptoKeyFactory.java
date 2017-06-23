package ome.smuggler.core.io.crypto;

import static java.util.Objects.requireNonNull;
import static util.error.Exceptions.runUnchecked;
import static util.error.Exceptions.unchecked;
import static util.string.Strings.readAsString;
import static util.string.Strings.requireString;

import javax.crypto.KeyGenerator;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;

/**
 * Factory methods for security {@link Key}s.
 */
public class CryptoKeyFactory {

    /**
     * Generates a security {@link Key} for the given algorithm.
     * @param algo the algorithm to use.
     * @return the key.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static Key generateKey(CryptoAlgoSpec algo) {
        requireNonNull(algo, "algo");

        return unchecked(() -> KeyGenerator.getInstance(algo.canonicalName())
                                           .generateKey()).get();
    }

    /**
     * Serialises a key into a given stream using Base64 encoding.
     * @param key the key to serialise.
     * @param out the destination stream to write the data to.
     * @throws NullPointerException if any argument is {@code null}.
     * <p>The following checked exceptions are rethrown as unchecked (i.e.
     * the exception is masked as a runtime exception and thrown as is without
     * wrapping it in a {@code RuntimeException}):
     * <br>{@link IOException} if an error occurs while writing to the stream.
     * </p>
     * @see #importKey(InputStream)
     */
    public static void exportKey(Key key, OutputStream out) {
        requireNonNull(key, "key");
        requireNonNull(out, "out");

        runUnchecked(() -> {
            try (OutputStream b64 = Base64.getEncoder().wrap(out);
                 ObjectOutputStream s = new ObjectOutputStream(b64)) {
                s.writeObject(key);
                s.flush();
            }
        });
    }

    /**
     * Serialises a key into a string using Base64 encoding.
     * This method simply calls {@link #exportKey(Key, OutputStream) exportKey},
     * capturing the output in a string.
     * @param key the key to serialise.
     * @return the serialised and Base64-encoded key.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static String exportKey(Key key) {
        requireNonNull(key, "key");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        exportKey(key, out);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());

        return unchecked(() -> readAsString(in)).get();
    }


    /**
     * De-serialises a key from a given Base64-encoded stream.
     * @param in contains the key object as serialised by the {@link
     * #exportKey(Key, OutputStream) export} method.
     * @return the key object read from the stream.
     * @throws NullPointerException if the argument is {@code null}.
     * <p>The following checked exceptions are rethrown as unchecked (i.e.
     * the exception is masked as a runtime exception and thrown as is without
     * wrapping it in a {@code RuntimeException}):
     * <br>{@link IOException} if an error occurs while reading from the stream.
     * <br>{@link ClassNotFoundException} if an error occurs while instantiating
     * the key object.
     * </p>
     * @see #exportKey(Key, OutputStream)
     */
    public static Key importKey(InputStream in) {
        requireNonNull(in, "in");

        return unchecked(() -> {
            try (InputStream b64 = Base64.getDecoder().wrap(in);
                 ObjectInputStream s = new ObjectInputStream(b64)) {
                return (Key) s.readObject();
            }
        }).get();
    }

    /**
     * De-serialises a key from a given Base64-encoded string.
     * @param encodedKey contains the key object as serialised by the {@link
     * #exportKey(Key, OutputStream) export} method.
     * @return the key object read from the stream.
     * @throws NullPointerException if the argument is {@code null}.
     * <p>The following checked exceptions are rethrown as unchecked (i.e.
     * the exception is masked as a runtime exception and thrown as is without
     * wrapping it in a {@code RuntimeException}):
     * <br>{@link IOException} if an error occurs while reading from the string.
     * <br>{@link ClassNotFoundException} if an error occurs while instantiating
     * the key object.
     * </p>
     * @see #exportKey(Key, OutputStream)
     * @see #exportNewKey(CryptoAlgoSpec)
     */
    public static Key importKey(String encodedKey) {
        requireString(encodedKey, "encodedKey");

        byte[] data = encodedKey.getBytes(StandardCharsets.UTF_8);
        return importKey(new ByteArrayInputStream(data));
    }

    /**
     * Generates a new key and serialises it using Base64 encoding.
     * This method combines {@link #generateKey(CryptoAlgoSpec) generateKey}
     * and {@link #exportKey(Key, OutputStream) exportKey}, capturing the
     * output in a string.
     * @param algo the algorithm to use.
     * @return the serialised and Base64-encoded key.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static String exportNewKey(CryptoAlgoSpec algo) {
        Key key = generateKey(algo);
        return exportKey(key);
    }

}

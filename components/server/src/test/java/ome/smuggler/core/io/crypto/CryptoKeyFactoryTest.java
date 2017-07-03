package ome.smuggler.core.io.crypto;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.junit.Assert.*;
import static ome.smuggler.core.io.crypto.CryptoKeyFactory.*;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.io.*;
import java.security.Key;


@RunWith(Theories.class)
public class CryptoKeyFactoryTest {

    @DataPoints
    public static CryptoAlgoSpec[] algos = CryptoAlgoSpec.values();

    private static Key serializeThenDeserialize(Key input) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        exportKey(input, out);

        ByteArrayInputStream serialized =
                new ByteArrayInputStream(out.toByteArray());
        return importKey(serialized);
    }

    private static void assertCanEncrypt(CipherFactory crypto) {
        CryptoSinkWriter<byte[]> encryptionFilter =
                new CryptoSinkWriter<>(crypto, OutputStream::write);

        byte[] input = new byte[] { 10 };
        ByteArrayOutputStream sink = new ByteArrayOutputStream();
        encryptionFilter.uncheckedWrite(sink, input);
        byte[] encrypted = sink.toByteArray();

        assertThat(input, not(equalTo(encrypted)));
        // compares array lengths and elements
    }

    @Theory
    public void serializeThenDeserializeIsIdentity(CryptoAlgoSpec algo) {
        Key initialKey = generateKey(algo);
        Key deserializedKey = serializeThenDeserialize(initialKey);

        assertArrayEquals(initialKey.getEncoded(),
                          deserializedKey.getEncoded());
    }

    @Theory
    public void canImportKeyExportedToString(CryptoAlgoSpec algo) {
        String exported = exportNewKey(algo);
        Key key = importKey(exported);

        assertCanEncrypt(new CipherFactory(algo, key));
    }

    @Test (expected = NullPointerException.class)
    public void generateKeyThrowsIfNullAlgo() {
        generateKey(null);
    }

    @Test (expected = NullPointerException.class)
    public void exportKeyThrowsIfNullKey() {
        exportKey(null, new ByteArrayOutputStream());
    }

    @Test (expected = NullPointerException.class)
    public void exportKeyThrowsIfNullStream() {
        exportKey(mock(Key.class), null);
    }

    @Test (expected = NullPointerException.class)
    public void importKeyThrowsIfNullStream() {
        importKey((InputStream) null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void importKeyThrowsIfNullString() {
        importKey((String) null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void importKeyThrowsIfEmptyString() {
        importKey("");
    }

    @Test (expected = IOException.class)
    public void importKeyThrowsIfStringContentNotBase64() {
        importKey("not base 64");
    }

    @Test (expected = NullPointerException.class)
    public void exportNewKeyThrowsIfNullAlgo() {
        exportNewKey(null);
    }

    @Test (expected = NullPointerException.class)
    public void exportKeyToStringThrowsIfNullKey() {
        exportKey(null);
    }

}

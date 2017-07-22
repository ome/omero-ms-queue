package util.io.crypto;

import static org.junit.Assert.*;

import util.io.StreamOps;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;


@RunWith(Theories.class)
public class CryptoSourceReaderTest {

    @DataPoints
    public static byte[][] inputs = new byte[][] {
            new byte[0], new byte[] { 1 }, new byte[] { 1, 2 },
            new byte[] { 1, 2, 3 }
    };


    private CipherFactory crypto;
    private CryptoSinkWriter<byte[]> encryptionFilter;
    private CryptoSourceReader<byte[]> decryptionFilter;

    @Before
    public void setup() {
        CryptoAlgoSpec algo = CryptoAlgoSpec.AES;
        crypto = new CipherFactory(algo, CryptoKeyFactory.generateKey(algo));
        encryptionFilter =
                new CryptoSinkWriter<>(crypto, OutputStream::write);
        decryptionFilter =
                new CryptoSourceReader<>(crypto, StreamOps::readAll);
    }

    @Theory
    public void writeThenReadIsIdentity(byte[] input) throws Exception {
        ByteArrayOutputStream sink = new ByteArrayOutputStream();
        encryptionFilter.write(sink, input);
        byte[] encrypted = sink.toByteArray();

        ByteArrayInputStream source = new ByteArrayInputStream(encrypted);
        byte[] decrypted = decryptionFilter.read(source);

        assertArrayEquals(input, decrypted);
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullCrypto() {
        new CryptoSourceReader<>(null, s -> "");
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullReader() {
        new CryptoSourceReader<String>(crypto, null);
    }

    @Test (expected = NullPointerException.class)
    public void readThrowsIfNullStream() {
        decryptionFilter.uncheckedRead(null);
    }

}

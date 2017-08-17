package util.io.crypto;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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
    public static final byte[][] inputs = new byte[][] {
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

    private ByteArrayInputStream encrypt(byte[] input) throws Exception {
        ByteArrayOutputStream sink = new ByteArrayOutputStream();
        encryptionFilter.write(sink, input);
        byte[] encrypted = sink.toByteArray();

        return new ByteArrayInputStream(encrypted);
    }

    @Theory
    public void writeThenReadIsIdentity(byte[] input) throws Exception {
        ByteArrayInputStream source = encrypt(input);
        byte[] decrypted = decryptionFilter.read(source);

        assertArrayEquals(input, decrypted);
    }

    @Test (expected = IllegalArgumentException.class)
    public void readerExceptionBubblesUp() throws Exception {
        ByteArrayInputStream source = encrypt(inputs[2]);
        CryptoSourceReader<byte[]> target = new CryptoSourceReader<>(
                crypto, in -> { throw new IllegalArgumentException(); });
        target.read(source);
    }

    @Test (expected = IllegalArgumentException.class)
    public void streamCreationExceptionBubblesUp() throws Exception {
        ByteArrayInputStream source = encrypt(inputs[2]);
        CipherFactory factory = mock(CipherFactory.class);
        when(factory.decryptionChipher()).thenThrow(
                new IllegalArgumentException());

        CryptoSourceReader<byte[]> target = new CryptoSourceReader<>(
                                                factory, StreamOps::readAll);
        target.read(source);
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

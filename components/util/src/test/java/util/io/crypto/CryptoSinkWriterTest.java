package util.io.crypto;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class CryptoSinkWriterTest {

    private static CipherFactory crypto() {
        CryptoAlgoSpec algo = CryptoAlgoSpec.AES;
        return new CipherFactory(algo,
                                 CryptoKeyFactory.generateKey(algo));
    }

    private static CryptoSinkWriter<Byte> cryptoFilter() {
        return new CryptoSinkWriter<>(crypto(), OutputStream::write);
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullCrypto() {
        new CryptoSinkWriter<String>(null, (s, v) -> {});
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullWriter() {
        new CryptoSinkWriter<String>(crypto(), null);
    }

    @Test (expected = NullPointerException.class)
    public void writeThrowsIfNullStream() {
        cryptoFilter().uncheckedWrite(null, (byte)10);
    }

    @Test (expected = NullPointerException.class)
    public void writeThrowsIfNullValue() {
        cryptoFilter().uncheckedWrite(new ByteArrayOutputStream(), null);
    }

    @Test
    public void encryptValue() {
        byte value = 10;
        ByteArrayOutputStream sink = new ByteArrayOutputStream();
        cryptoFilter().uncheckedWrite(sink, value);
        byte[] encrypted = sink.toByteArray();

        assertThat(encrypted.length, greaterThan(0));
        if (encrypted.length == 1) {
            assertThat(encrypted[0], is(not(value)));
        }
    }

    @Test (expected = IOException.class)
    public void writerExceptionBubblesUp() {
        CryptoSinkWriter<Byte> target = new CryptoSinkWriter<>(
                crypto(), (out, v) -> { throw new IOException(); });
        ByteArrayOutputStream sink = new ByteArrayOutputStream();
        byte value = 10;

        target.uncheckedWrite(sink, value);
    }

    @Test (expected = IllegalArgumentException.class)
    public void streamCreationExceptionBubblesUp() {
        CipherFactory factory = mock(CipherFactory.class);
        when(factory.encryptionChipher())
                .thenThrow(new IllegalArgumentException());

        CryptoSinkWriter<Byte> target = new CryptoSinkWriter<>(
                                                factory, OutputStream::write);
        ByteArrayOutputStream sink = new ByteArrayOutputStream();
        byte value = 10;

        target.uncheckedWrite(sink, value);
    }

}

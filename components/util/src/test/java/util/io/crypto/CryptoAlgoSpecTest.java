package util.io.crypto;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.io.crypto.CryptoAlgoSpec.*;

import org.junit.Test;

public class CryptoAlgoSpecTest {

    @Test
    public void checkAesSpec() {
        assertThat(AES.spec(), is("AES"));
    }

    @Test
    public void checkAesCanonicalName() {
        assertThat(AES.canonicalName(), is("AES"));
    }

    @Test
    public void checkAES_ECB_PKCS5PaddingSpec() {
        assertThat(AES_ECB_PKCS5Padding.spec(),
                   is("AES/ECB/PKCS5Padding"));
    }

    @Test
    public void checkAES_ECB_PKCS5PaddingCanonicalName() {
        assertThat(AES_ECB_PKCS5Padding.canonicalName(), is("AES"));
    }

}

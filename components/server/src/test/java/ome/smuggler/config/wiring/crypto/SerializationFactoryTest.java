package ome.smuggler.config.wiring.crypto;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.sequence.Arrayz.array;

import ome.smuggler.config.data.CryptoYmlFile;
import ome.smuggler.config.items.CryptoConfig;
import ome.smuggler.core.types.CryptoConfigReader;
import ome.smuggler.core.types.CryptoConfigSource;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@RunWith(Theories.class)
public class SerializationFactoryTest {

    private static CryptoConfigSource reader(boolean encryptionOn) {
        CryptoConfig validConfigWithKey = new CryptoYmlFile().first();
        validConfigWithKey.setEncrypt(encryptionOn);
        return new CryptoConfigReader(validConfigWithKey);
    }

    @DataPoints
    public static CryptoConfigSource[] configs =
            array(reader(true), reader(false));

    @DataPoints
    public static String[] values = array("", "1", "1 2", "1@.2.3");

    @Theory
    public void serializeThenDeserializeIsIdentity(CryptoConfigSource config,
                                                   String value)
            throws Exception{
        SerializationFactory sf = new SerializationFactory(config);

        ByteArrayOutputStream sink = new ByteArrayOutputStream();
        sf.serializer().write(sink, value);

        ByteArrayInputStream source =
                new ByteArrayInputStream(sink.toByteArray());
        String deserialized = sf.deserializer(String.class).read(source);

        assertThat(deserialized, is(value));
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullConfig() {
        new SerializationFactory(null);
    }

}

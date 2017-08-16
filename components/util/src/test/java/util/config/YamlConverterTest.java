package util.config;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.io.*;

public class YamlConverterTest {

    private YamlConverter<Integer> target;

    @Before
    public void setup() {
        target = new YamlConverter<>();
    }

    @Test
    public void serializeThenDeserializeIsIdentity() {
        int data = 123;
        String serialized = target.toYaml(data);
        InputStream serializedBytes =
                new ByteArrayInputStream(serialized.getBytes());
        int actual = target.fromYaml(serializedBytes, Integer.class);

        assertThat(actual, is(data));
    }

    @Test
    public void serialize2ThenDeserializeIsIdentity() {
        int data = 123;
        StringWriter out = new StringWriter();
        target.toYaml(data, out);
        InputStream serializedBytes =
                new ByteArrayInputStream(out.toString().getBytes());
        int actual = target.fromYaml(serializedBytes, Integer.class);

        assertThat(actual, is(data));
    }

    @Test
    public void readEmptyList() {
        String yaml = "[]";
        InputStream serializedBytes =
                new ByteArrayInputStream(yaml.getBytes());
        Integer[] actual = target.fromYamlList(serializedBytes)
                                 .toArray(Integer[]::new);

        assertThat(actual.length, is(0));
    }

    @Test
    public void readList() {
        String yaml = "[1, 2]";
        InputStream serializedBytes =
                new ByteArrayInputStream(yaml.getBytes());
        Integer[] actual = target.fromYamlList(serializedBytes)
                .toArray(Integer[]::new);

        assertThat(actual.length, is(2));
        assertThat(actual[0], is(1));
        assertThat(actual[1], is(2));
    }

    @Test (expected = ClassCastException.class)
    public void cantReadListField() {
        String yaml = "test: [1, 2]";  // (*)
        InputStream serializedBytes =
                new ByteArrayInputStream(yaml.getBytes());
        target.fromYamlList(serializedBytes);
    }
    /* (*) note this is a field named "test" with a value of "[1, 2]".
     * So Yaml reads it into a LinkedHashSet, which is right cos it's
     * not a bare list.
     */

    @Test (expected = NullPointerException.class)
    public void toYamlThrowsIfNullData() {
        target.toYaml(null);
    }

    @Test (expected = NullPointerException.class)
    public void toYaml2ThrowsIfNullData() {
        target.toYaml(null, new StringWriter());
    }

    @Test (expected = NullPointerException.class)
    public void toYaml2ThrowsIfNullWriter() {
        target.toYaml(1, null);
    }

    @Test (expected = NullPointerException.class)
    public void fromYamlListThrowsIfNullInput() {
        target.fromYamlList(null);
    }

    @Test (expected = NullPointerException.class)
    public void fromYamlThrowsIfNullInput() {
        target.fromYaml(null, Integer.class);
    }

}

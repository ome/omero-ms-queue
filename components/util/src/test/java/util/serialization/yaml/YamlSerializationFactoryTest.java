package util.serialization.yaml;

import org.junit.Test;
import util.serialization.SerializationFactory;
import util.serialization.WriteReadTest;


public class YamlSerializationFactoryTest extends WriteReadTest {

    private boolean x;
    private long y;

    public boolean isX() {
        return x;
    }

    public void setX(boolean x) {
        this.x = x;
    }

    public long getY() {
        return y;
    }

    public void setY(long y) {
        this.y = y;
    }


    @Override
    public boolean equals(Object t) {
        if (t instanceof YamlSerializationFactoryTest) {
            YamlSerializationFactoryTest u = (YamlSerializationFactoryTest) t;
            return x == u.x && y == u.y;
        }
        return false;
    }


    @Override
    protected SerializationFactory factory() {
        return new YamlSerializationFactory();
    }

    @Test
    public void serializeAndDeserialize() {
        YamlSerializationFactoryTest initialValue =
                new YamlSerializationFactoryTest();
        initialValue.x = true;
        initialValue.y = 1L;

        assertWriteThenReadGivesInitialValue(
                initialValue, YamlSerializationFactoryTest.class);
    }
}

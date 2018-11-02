package util.serialization.json;

import org.junit.Test;
import util.serialization.SerializationFactory;
import util.serialization.WriteReadTest;
import util.types.FutureTimepoint;
import util.types.Schedule;

public class JsonSerializationFactoryTest extends WriteReadTest {

    @Override
    protected SerializationFactory factory() {
        return new JsonSerializationFactory();
    }

    @Test
    public void serializeAndDeserialize() {
        Schedule<Double> initialValue =  // (*)
                new Schedule<>(FutureTimepoint.now(), 1.0);

        assertWriteThenReadGivesInitialValue(initialValue, Schedule.class);
    }
    // (*) Be careful when serialising generics! Have a look at GenericsTest!
}

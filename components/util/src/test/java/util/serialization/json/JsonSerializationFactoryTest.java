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
    /* (*) Be careful when serialising generics!
     * To make sure you can de-serialise it, you should use the Gson type
     * token rather than the raw class type otherwise Gson will have to guess
     * the generic type, courtesy of Java type erasure. In fact, if you had a
     * Long instead of a Double, Gson would de-serialise it back to an object
     * of type Schedule<Double>!
     * In fact, the serialised JSON would look something like:
     *
     *   { "when":{"wrappedValue":{"seconds":1517746022,"nanos":920000000}}
     *   , "what":1
     *   }
     *
     * Since JSON makes no distinction between integers and real numbers, Gson
     * defaults to de-serialising the "what" to Double.
     */
}

package util.serialization.json;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import com.google.gson.reflect.TypeToken;
import org.junit.Test;
import util.types.FutureTimepoint;
import util.types.Schedule;


public class GenericsTest extends JsonWriteReadTest {

    @Test
    public void serializeAndDeserializeRaw() {
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
     *
     * The tests below show the type erasure issues and how to overcome them
     * with a Gson type token.
     */

    @Test
    @SuppressWarnings("unchecked")
    public void serializeAndDeserializeTypeErasure() {
        Schedule<Long> initialValue =
                new Schedule<>(FutureTimepoint.now(), 1L);
        Class<Schedule<Long>> valueType = (Class<Schedule<Long>>)
                initialValue.getClass();  // (1)

        Schedule<Long> readValue = writeThenRead(initialValue, valueType); //(2)
        try {
            Long what = readValue.what();
            fail("read a double into a long: " + what);
        } catch (ClassCastException e) {
            assertThat(e.getMessage(),
                       is("java.lang.Double cannot be cast to java.lang.Long"));
        }
    }
    /* (1) This won't help cos after type erasure you have a raw Schedule as
     * if you used Schedule.class like in the test above.
     * (2) Will compile, but bomb out at runtime since readValue will be
     * of type Schedule<Double> when de-serialised back---read comments
     * above for the details.
     */

    @Test
    public void serializeAndDeserializeOvercomeTypeErasure() {
        Schedule<Long> initialValue =
                new Schedule<>(FutureTimepoint.now(), 1L);
        TypeToken<Schedule<Long>> typeToken = new TypeToken<Schedule<Long>>(){};

        assertWriteThenReadGivesInitialValue(initialValue, typeToken);
    }

}

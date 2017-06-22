package integration.serialization;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Optional;
import org.junit.Test;
import com.google.gson.reflect.TypeToken;

public class OptionalIntegerTest extends JsonWriteReadTest {
    
    private void assertSerializeAndDeserialize(Optional<Integer> initialValue) {
        TypeToken<Optional<Integer>> valueType = 
                new TypeToken<Optional<Integer>>(){};
        assertWriteThenReadGivesInitialValue(initialValue, valueType);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void deserializesDoubleDueToTypeErasure() {
        Optional<Integer> initialValue = Optional.of(1);
        Class<Optional<Integer>> valueType = (Class<Optional<Integer>>) 
                                                initialValue.getClass(); 
        Optional<Integer> readValue = writeThenRead(initialValue, valueType);
        
        assertThat(readValue, is(not(initialValue)));
        assertThat(readValue, is(Optional.of(1.0)));  // (*)
    }
    /* (*) Type erasure goodness!
     * So what's happening here? 
     * 
     * 1. Gson serializes initialValue to: { value: 1 }
     * 2. Gson deserializes it to Optional<Double> as it cannot know we want 1 
     *    to be an Integer.
     * 3. So the actual type of readValue.value is Double; the fact we declared
     *    it as Optional<Integer> makes no difference due to type erasure. 
     * 
     * What's not to like?
     * Fortunately Gson has a workaround for this (i.e. TypeToken), see below.
     */
    
    @Test
    public void serializeAndDeserializeValue() {
        assertSerializeAndDeserialize(Optional.of(1));
    }
    
    @Test
    public void serializeAndDeserializeEmpty() {
        assertSerializeAndDeserialize(Optional.empty());
    }
    
}

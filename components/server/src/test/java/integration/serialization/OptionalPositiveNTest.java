package integration.serialization;

import static ome.smuggler.core.types.ValueParserFactory.positiveInt;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Optional;

import util.types.PositiveN;

import org.junit.Test;

import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

public class OptionalPositiveNTest extends JsonWriteReadTest {
    
    private void assertSerializeAndDeserialize(Optional<PositiveN> initialValue) {
        TypeToken<Optional<PositiveN>> valueType = 
                new TypeToken<Optional<PositiveN>>(){};
        assertWriteThenReadGivesInitialValue(initialValue, valueType);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void deserializesMapDueToTypeErasure() {
        PositiveN one = positiveInt("1").getRight();
        Optional<PositiveN> initialValue = Optional.of(one);
        Class<Optional<PositiveN>> valueType = (Class<Optional<PositiveN>>) 
                                                initialValue.getClass(); 
        Optional<PositiveN> readValue = writeThenRead(initialValue, valueType);
        
        assertThat(readValue, is(not(initialValue)));
        
        LinkedTreeMap<String, Double> deserialized = new LinkedTreeMap<>();
        deserialized.put("wrappedValue", 1.0);
        assertThat(readValue, is(Optional.of(deserialized)));  // (*)
    }
    /* (*) Type erasure goodness!
     * So what's happening here? 
     * 
     * 1. Gson serializes initialValue to: { value: { wrappedValue: 1 } }
     * 2. Gson deserializes it to Optional<LinkedTreeMap<String, Double>> as it
     *    cannot know we want 1 to be a Long and value to be a PositiveN; the
     *    map has a key of  "wrappedValue" with corresponding value of 1.0.
     * 3. The actual type of readValue.value is LinkedTreeMap<String, Double>; 
     *    the fact we declared it as Optional<PositiveN> makes no difference 
     *    due to type erasure. 
     * 
     * What's not to like?
     * Fortunately Gson has a workaround for this (i.e. TypeToken), see below.
     */
    
    @Test
    public void serializeAndDeserializeValue() {
        PositiveN one = positiveInt("1").getRight();
        assertSerializeAndDeserialize(Optional.of(one));
    }
    
    @Test
    public void serializeAndDeserializeEmpty() {
        assertSerializeAndDeserialize(Optional.empty());
    }
    
}

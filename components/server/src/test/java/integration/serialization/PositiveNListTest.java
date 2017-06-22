package integration.serialization;

import static ome.smuggler.core.types.ValueParserFactory.positiveInt;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ome.smuggler.core.types.PositiveN;

import org.junit.Test;

import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;


public class PositiveNListTest extends JsonWriteReadTest {

    private static PositiveN posN(Integer x) {
        return positiveInt(x.toString()).getRight();
    }

    private void assertSerializeAndDeserialize(List<PositiveN> initialValue) {
        TypeToken<List<PositiveN>> valueType = 
                new TypeToken<List<PositiveN>>(){};
        assertWriteThenReadGivesInitialValue(initialValue, valueType);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void deserializesMapsDueToTypeErasure() {
        List<PositiveN> initialValue = Collections.singletonList(posN(1));
        Class<List<PositiveN>> valueType = (Class<List<PositiveN>>) 
                                                initialValue.getClass();
        List<PositiveN> readValue = writeThenRead(initialValue, valueType);
        
        assertThat(readValue, is(not(initialValue)));
        
        LinkedTreeMap<String, Double> deserialized =
                new LinkedTreeMap<>();
        deserialized.put("wrappedValue", 1.0);
        
        assertThat(readValue, is(Collections.singletonList(deserialized)));  // (*)
    }
    /* (*) Type erasure goodness!
     * So what's happening here? 
     * 
     * 1. Gson serializes initialValue to: [ { wrappedValue: 1 } ]
     * 2. Gson deserializes it to List<LinkedTreeMap<String, Double>> as it 
     *    cannot know what we want due to type erasure. 
     * 3. The actual type of readValue[0] is LinkedTreeMap<String, Double>; 
     *    the fact we declared it as List<PositiveN> makes no difference due 
     *    to type erasure. 
     * 
     * What's not to like?
     * Fortunately Gson has a workaround for this (i.e. TypeToken), see below.
     */
    
    @Test
    public void serializeAndDeserializeValue() {
        assertSerializeAndDeserialize(Arrays.asList(posN(1), posN(2)));
    }
    
    @Test
    public void serializeAndDeserializeEmpty() {
        assertSerializeAndDeserialize(Collections.emptyList());
    }
    
}

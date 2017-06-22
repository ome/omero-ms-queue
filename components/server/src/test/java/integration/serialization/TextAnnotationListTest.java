package integration.serialization;

import static ome.smuggler.core.types.ValueParserFactory.textAnnotation;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ome.smuggler.core.types.TextAnnotation;

import org.junit.Test;

import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;


public class TextAnnotationListTest extends JsonWriteReadTest {

    private static TextAnnotation anno(String ns, String txt) {
        return textAnnotation(ns, txt).getRight();
    }

    private void assertSerializeAndDeserialize(List<TextAnnotation> initialValue) {
        TypeToken<List<TextAnnotation>> valueType = 
                new TypeToken<List<TextAnnotation>>(){};
        assertWriteThenReadGivesInitialValue(initialValue, valueType);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void deserializesMapsDueToTypeErasure() {
        List<TextAnnotation> initialValue = Collections.singletonList(anno("n", "t"));
        Class<List<TextAnnotation>> valueType = (Class<List<TextAnnotation>>) 
                                                initialValue.getClass();
        List<TextAnnotation> readValue = writeThenRead(initialValue, valueType);
        
        assertThat(readValue, is(not(initialValue)));
        
        LinkedTreeMap<String, LinkedTreeMap<String, String>> deserialized =
                new LinkedTreeMap<>();
        LinkedTreeMap<String, String> deserializedAnno =
                new LinkedTreeMap<>();
        deserializedAnno.put("fst", "n");
        deserializedAnno.put("snd", "t");
        deserialized.put("wrappedValue", deserializedAnno);
        
        assertThat(readValue, is(Collections.singletonList(deserialized)));  // (*)
    }
    /* (*) Type erasure goodness!
     * So what's happening here? 
     * 
     * 1. Gson serializes initialValue to: [ { wrappedValue: { fst: n, snd: t } } ]
     * 2. Gson deserializes it to 
     *      List<LinkedTreeMap<String, LinkedTreeMap<String, String>>> 
     *    as it cannot know what we want due to type erasure. 
     * 3. The actual type of readValue[0] is 
     *      LinkedTreeMap<String, LinkedTreeMap<String, String>>; 
     *    the fact we declared it as List<TextAnnotation> makes no difference 
     *    due to type erasure. 
     * 
     * What's not to like?
     * Fortunately Gson has a workaround for this (i.e. TypeToken), see below.
     */
    
    @Test
    public void serializeAndDeserializeValue() {
        TextAnnotation a1 = anno("n1", "t1"), a2 = anno("n2", "t2");
        assertSerializeAndDeserialize(Arrays.asList(a1, a2));
    }
    
    @Test
    public void serializeAndDeserializeEmpty() {
        assertSerializeAndDeserialize(Collections.emptyList());
    }
    
}

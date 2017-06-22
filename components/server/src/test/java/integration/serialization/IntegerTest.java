package integration.serialization;

import org.junit.Test;

import com.google.gson.reflect.TypeToken;

public class IntegerTest extends JsonWriteReadTest {

    private int primitiveField;
    
    @Test
    public void jsonSerializeAndDeserialize() {
        Integer initialValue = 1;
        
        assertWriteThenReadGivesInitialValue(initialValue, Integer.class);
        assertWriteThenReadGivesInitialValue(initialValue, 
                                             new TypeToken<Integer>(){});
    }
    
    @Test
    public void jsonSerializeAndDeserializeClassWithIntField() {
        IntegerTest initialValue = new IntegerTest();
        initialValue.primitiveField = 1;
        
        assertWriteThenReadGivesInitialValue(initialValue, IntegerTest.class);
        assertWriteThenReadGivesInitialValue(initialValue, 
                                             new TypeToken<IntegerTest>(){});
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IntegerTest) {
            IntegerTest other = (IntegerTest) obj;
            return primitiveField == other.primitiveField;
        }
        return false;
    }
    
}

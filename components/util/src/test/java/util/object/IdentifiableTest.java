package util.object;

import static org.junit.Assert.*;

import org.junit.Test;


public class IdentifiableTest {

    class X implements Identifiable { }
    class Y implements Identifiable { }
    
    @Test
    public void differentInstancesOfSameClassHaveDifferentIds() {
        String x1 = new X().id(), 
               x2 = new X().id();
        assertNotEquals(x1, x2);
    }
    
    @Test
    public void instancesOfDifferentClassesHaveDifferentIds() {
        String x = new X().id(),
               y = new Y().id();
        assertNotEquals(x, y);
    }
    
}

package kew.core.msg;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;


/**
 * This test is just to make sure we get a failure if we add a new constant
 * to the enum, hoping we'll then remember that any implementation that
 * has to switch on those constants need updating too!
 */
public class RepeatActionTest {

    @Test
    public void knownConstants() {
        RepeatAction[] vs = RepeatAction.values();
        assertThat(vs.length, is(2));
    }

    @Test
    public void durableConstant() {
        RepeatAction c = RepeatAction.valueOf("Repeat");
        assertNotNull(c);
    }

    @Test
    public void nonDurableConstant() {
        RepeatAction c = RepeatAction.valueOf("Stop");
        assertNotNull(c);
    }

}

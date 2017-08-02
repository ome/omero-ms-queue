package kew.core.qchan.spi;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

/**
 * This test is just to make sure we get a failure if we add a new constant
 * to the enum, hoping we'll then remember that the QMsgFactory implementations
 * have to switch on those constants, so they probably need updating too!
 */
public class QMessageTypeTest {

    @Test
    public void knownConstants() {
        QMessageType[] vs = QMessageType.values();
        assertThat(vs.length, is(2));
    }

    @Test
    public void durableConstant() {
        QMessageType c = QMessageType.valueOf("Durable");
        assertNotNull(c);
    }

    @Test
    public void nonDurableConstant() {
        QMessageType c = QMessageType.valueOf("NonDurable");
        assertNotNull(c);
    }

}

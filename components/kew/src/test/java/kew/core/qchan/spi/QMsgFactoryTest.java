package kew.core.qchan.spi;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class QMsgFactoryTest {

    public static final Integer DurableMsg = 1;
    public static final Integer NonDurableMsg = 2;

    public static QMsgFactory<Integer> factory() {
        return t -> t.equals(QMessageType.Durable) ? DurableMsg
                                                   : NonDurableMsg;
    }

    @Test
    public void canBuildDurableMessage() {
        Integer actual = factory().durableMessage();

        assertNotNull(actual);
        assertThat(actual, is(DurableMsg));
    }

    @Test
    public void canBuildNonDurableMessage() {
        Integer actual = factory().nonDurableMessage();

        assertNotNull(actual);
        assertThat(actual, is(NonDurableMsg));
    }

}

package kew.core.qchan.spi;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import java.util.function.Consumer;
import java.util.function.Function;

public class QMsgBuilderTest {

    private static QMsgFactory<Integer> qMsgFactory(int seed) {
        return t -> seed;
    }

    private static QMsgBuilder<Integer> qMsgBuilder() {
        return QMsgFactory::nonDurableMessage;
    }

    @Test
    public void withTransformer() {
        int seed = 1;
        int result = qMsgBuilder().with(x -> x + 1).apply(qMsgFactory(seed));

        assertThat(result, is(2));
    }

    @Test
    public void withSetter() {
        int seed = 1;
        Consumer<Integer> setter = x -> assertThat(x, is(seed));
        int result = qMsgBuilder().with(setter).apply(qMsgFactory(seed));

        assertThat(result, is(seed));
    }

    @Test (expected = NullPointerException.class)
    public void withTransformerThrowsIfNullArg() {
        qMsgBuilder().with((Function<Integer, Integer>) null);
    }

    @Test (expected = NullPointerException.class)
    public void withSetterThrowsIfNullArg() {
        qMsgBuilder().with((Consumer<Integer>) null);
    }

}

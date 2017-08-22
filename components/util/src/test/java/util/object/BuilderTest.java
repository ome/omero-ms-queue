package util.object;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


public class BuilderTest {

    private static class Target extends HashMap<String, Integer> {
        String seed;
        Target(String seed) {
            this.seed = seed;
        }
    }

    private static Function<Target, Target> fieldOf(String name, int value) {
        return m -> {
            m.put(name, value);
            return m;
        };
    }

    private static Consumer<Target> setField(String name, int value) {
        return m -> m.put(name, value);
    }


    @Test
    public void makeWithFactoryFun() {
        String seed = "s";
        Target expected = new Target(seed);
        Target actual = Builder.make(Target::new).apply(seed);

        assertThat(actual, is(expected));
        assertThat(actual.seed, is(seed));
    }

    @Test
    public void makeWithSupplier() {
        String seed = "s";
        Target expected = new Target(seed);
        Target actual = Builder.make(() -> new Target(seed)).apply(null);

        assertThat(actual, is(expected));
        assertThat(actual.seed, is(seed));
    }

    @Test
    public void buildWithOneTransform() {
        String seed = "s";
        Target expected = new Target(seed);
        expected.put("k", 1);

        Target actual = Builder.make(Target::new)
                               .with(fieldOf("k", 1))
                               .apply(seed);

        assertThat(actual, is(expected));
        assertThat(actual.seed, is(seed));
    }

    @Test
    public void buildWithOneConsumer() {
        String seed = "s";
        Target expected = new Target(seed);
        expected.put("k", 1);

        Target actual = Builder.make(Target::new)
                               .with(setField("k", 1))
                               .apply(seed);

        assertThat(actual, is(expected));
        assertThat(actual.seed, is(seed));
    }

    @Test
    public void buildWithManyTransformsAndConsumers() {
        String seed = "s";
        Target expected = new Target(seed);
        expected.put("k1", 1);
        expected.put("k2", 2);
        expected.put("k3", 3);

        Target actual = Builder.make(Target::new)
                               .with(fieldOf("k1", 1))
                               .with(setField("k2", 2))
                               .with(fieldOf("k3", 3))
                               .apply(seed);

        assertThat(actual, is(expected));
        assertThat(actual.seed, is(seed));
    }

    @Test
    public void buildValuesFunctionalStyle() {
        Integer actual = Builder.make(() -> 1)
                                .with(x -> x + 1)
                                .with(x -> x * 2)
                                .apply(null);
        assertThat(actual, is(4));
    }

    @Test (expected = NullPointerException.class)
    public void makeThrowsIfNullFactoryFun() {
        Builder.make((Function<String, Integer>) null);
    }

    @Test (expected = NullPointerException.class)
    public void makeThrowsIfNullFactorySupplier() {
        Builder.make((Supplier<Integer>) null);
    }

    @Test (expected = NullPointerException.class)
    public void withThrowsIfNullTransformer() {
        Builder.make(x -> 1)
               .with((Function<Integer, Integer>) null);
    }

    @Test (expected = NullPointerException.class)
    public void withThrowsIfNullSetter() {
        Builder.make(x -> 1)
               .with((Consumer<Integer>) null);
    }

}

package kew.providers.artemis.config;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static kew.providers.artemis.config.QueueConfig.*;

import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.config.CoreQueueConfiguration;

import org.junit.Test;

import java.util.function.Supplier;

public class QueueConfigTest {

    private static CoreQueueConfiguration qConfig(int id) {
        return new CoreQueueConfiguration()
              .setAddress("address" + id)
              .setName("name" + id);
    }

    private static void assertHasQueue(Configuration cfg,
                                       CoreQueueConfiguration...qs) {
        for (CoreQueueConfiguration q : qs) {
            assertThat(cfg.getQueueConfigurations(), hasItem(q));
        }
    }


    @Test
    public void addQueueToCoreConfig() {
        CoreQueueConfiguration test = qConfig(1);
        Configuration cfg = CoreConfigFactory.empty()
                                             .with(q(test))
                                             .apply(null);
        assertHasQueue(cfg, test);
    }

    @Test
    public void addQueuesToCoreConfig() {
        CoreQueueConfiguration q1 = qConfig(1);
        CoreQueueConfiguration q2 = qConfig(2);

        Configuration cfg = CoreConfigFactory.empty()
                                             .with(q(q1, q2))
                                             .apply(null);
        assertHasQueue(cfg, q1, q2);
    }

    @Test
    public void addQueueUsingSupplier() {
        CoreQueueConfiguration test = qConfig(1);
        Configuration cfg = CoreConfigFactory.empty()
                                             .with(q(() -> test))
                                             .apply(null);
        assertHasQueue(cfg, test);
    }

    @Test (expected = NullPointerException.class)
    public void qThrowsIfNullSupplier() {
        q((Supplier<CoreQueueConfiguration>) null);
    }

    @Test (expected = NullPointerException.class)
    public void qThrowsIfSupplierReturnsNull() {
        q(() -> null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void qThrowsIfNullQConfig() {
        q((CoreQueueConfiguration) null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void qThrowsIfNullQConfigArray() {
        q((CoreQueueConfiguration[]) null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void qThrowsIfQConfigArrayHasNull() {
        q(qConfig(1), null, qConfig(3));
    }

    @Test
    public void ctor() {
        new QueueConfig();  // only to get 100% coverage.
    }

}

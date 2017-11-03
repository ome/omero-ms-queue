package kew.providers.artemis.config;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static kew.providers.artemis.config.QueueConfig.*;

import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.config.CoreQueueConfiguration;

import org.junit.Test;

import java.util.function.Supplier;

public class QueueConfigTest {

    @Test
    public void addQueueToCoreConfig() {
        CoreQueueConfiguration test = new CoreQueueConfiguration()
                                     .setAddress("address")
                                     .setName("name");
        Configuration cfg = CoreConfigFactory.empty()
                                             .with(q(test))
                                             .apply(null);

        assertThat(cfg.getQueueConfigurations(), contains(test));
    }

    @Test (expected = NullPointerException.class)
    public void qThrowsIfNullSupplier() {
        q((Supplier<CoreQueueConfiguration>) null);
    }

    @Test (expected = NullPointerException.class)
    public void qThrowsIfSupplierReturnsNull() {
        q(() -> null);
    }

    @Test (expected = NullPointerException.class)
    public void qThrowsIfNullQConfig() {
        q((CoreQueueConfiguration) null);
    }

    @Test
    public void ctor() {
        new QueueConfig();  // only to get 100% coverage.
    }

}

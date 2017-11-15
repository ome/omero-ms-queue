package kew.providers.artemis.config.transport;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public abstract class BaseEndpointConfigTest<T extends EndpointConfig> {

    protected abstract T target();

    @Test
    public void generateUniqueTransportName() {
        String name1 = target().transport().getName();
        String name2 = target().transport().getName();

        assertThat(name1, not(isEmptyOrNullString()));
        assertThat(name2, not(isEmptyOrNullString()));
        assertThat(name1, not(name2));
    }

    @Test
    public void paramsNeverNull() {
        assertNotNull(target().params());
    }

}

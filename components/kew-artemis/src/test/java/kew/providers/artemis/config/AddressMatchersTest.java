package kew.providers.artemis.config;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class AddressMatchersTest {

    @Test
    public void matchPrefix() {
        String prefix = "some.address.prefix.";
        String actual = AddressMatchers.anyAddressStartingWith(prefix);
        assertThat(actual,
                   is(prefix + AddressMatchers.DefaultAnyWordsWildcard));
    }

    @Test
    public void matchAnyAddressUsingAnyWordsWildcard() {
        assertThat(AddressMatchers.anyAddress(),
                   is(AddressMatchers.DefaultAnyWordsWildcard));
    }

    @Test
    public void ctor() {
        new AddressMatchers();  // only to get 100% coverage.
    }

}

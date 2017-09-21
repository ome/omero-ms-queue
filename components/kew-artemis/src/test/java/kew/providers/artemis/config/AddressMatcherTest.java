package kew.providers.artemis.config;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class AddressMatcherTest {

    @Test
    public void matchPrefix() {
        String anyWordsWildcard = "!";
        AddressMatcher target = new AddressMatcher(anyWordsWildcard);

        String prefix = "some.address.prefix.";
        assertThat(target.anyAddressStartingWith(prefix),
                   is(prefix + anyWordsWildcard));
    }

    @Test
    public void matchAnyAddressUsingAnyWordsWildcard() {
        String anyWordsWildcard = "!";
        AddressMatcher target = new AddressMatcher(anyWordsWildcard);

        assertThat(target.anyAddress(), is(anyWordsWildcard));
    }

    @Test (expected = IllegalArgumentException.class)
    public void ctorThrowsIfNullAnyWordsWildcard() {
        new AddressMatcher(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void ctorThrowsIfEmptyAnyWordsWildcard() {
        new AddressMatcher("");
    }

}

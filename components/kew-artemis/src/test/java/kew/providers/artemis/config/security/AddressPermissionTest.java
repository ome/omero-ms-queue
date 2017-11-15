package kew.providers.artemis.config.security;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The only purpose of these tests is to get 100% coverage on the enum, so
 * to avoid noise in the coverage report.
 */
public class AddressPermissionTest {

    @Test
    public void serializeDeserializeEnumConstantsIsIdentity() {
        Set<AddressPermission> vs = Stream.of(AddressPermission.values())
                                          .collect(Collectors.toSet());
        Set<AddressPermission> ws = Stream.of(AddressPermission.values())
                                          .map(Object::toString)
                                          .map(AddressPermission::valueOf)
                                          .collect(Collectors.toSet());
        assertThat(vs, is(ws));
    }

}

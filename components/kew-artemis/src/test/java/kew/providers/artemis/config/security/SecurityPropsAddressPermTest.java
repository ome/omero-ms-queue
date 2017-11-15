package kew.providers.artemis.config.security;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static kew.providers.artemis.config.AddressMatchers.*;
import static kew.providers.artemis.config.security.AddressPermission.*;
import static kew.providers.artemis.config.security.RolePermissionsBuilder.role;
import static kew.providers.artemis.config.security.SecurityProps.addressPermissionsFor;

import kew.providers.artemis.config.CoreConfigFactory;
import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.security.Role;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class SecurityPropsAddressPermTest {

    @Test
    public void instantiateSecurityRolesIfNull() {
        Configuration cfg = CoreConfigFactory
            .empty()
            .with(c -> { c.setSecurityRoles(null); })
            .with(addressPermissionsFor(anyAddress(), role("r").can()))
            .apply(null);

        assertNotNull(cfg.getSecurityRoles());
        Set<Role> rs = cfg.getSecurityRoles().get(anyAddress());
        assertThat(rs, hasSize(1));
    }

    @Test
    public void addToExistingRolesForMatchedAddress() {
        String address = "x";

        Set<Role> existingRoles = new HashSet<>();
        Role existingRole = role("user").can(Send, Browse);
        existingRoles.add(existingRole);

        Role newRole = role("admin").can(Manage);

        Configuration cfg = CoreConfigFactory
                .empty()
                .with(c -> { c.putSecurityRoles(address, existingRoles); })
                .with(addressPermissionsFor(address, newRole))
                .apply(null);

        assertNotNull(cfg.getSecurityRoles());
        Set<Role> actualRoles = cfg.getSecurityRoles().get(address);
        existingRoles.add(newRole);
        assertThat(actualRoles, is(existingRoles));
    }

    @Test (expected = IllegalArgumentException.class)
    public void addressPermissionsForThrowsIfNullMatcher() {
        addressPermissionsFor(null, role("r").can());
    }

    @Test (expected = IllegalArgumentException.class)
    public void addressPermissionsForThrowsIfEmptyMatcher() {
        addressPermissionsFor("", role("r").can());
    }

    @Test (expected = NullPointerException.class)
    public void addressPermissionsForThrowsIfNullRole() {
        addressPermissionsFor(anyAddress(), null);
    }

}

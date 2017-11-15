package kew.providers.artemis.config.security;

import static java.util.stream.Collectors.toSet;
import static util.sequence.Arrayz.asStream;
import static util.sequence.Arrayz.requireArrayOfMinLength;
import static util.string.Strings.requireString;
import static kew.providers.artemis.config.security.AddressPermission.*;

import java.util.Set;

import org.apache.activemq.artemis.core.security.Role;

/**
 * Builds Artemis roles.
 */
public class RolePermissionsBuilder {

    /**
     * Creates a new builder for the given role name.
     * @param name the role name.
     * @return the builder.
     * @throws IllegalArgumentException if the argument is {@code null} or
     * empty.
     */
    public static RolePermissionsBuilder role(String name) {
        return new RolePermissionsBuilder(name);
    }

    private final String roleName;

    /**
     * Creates an instance to build roles having the specified name.
     * @param roleName the role name.
     * @throws IllegalArgumentException if the argument is {@code null} or
     * empty.
     */
    public RolePermissionsBuilder(String roleName) {
        requireString(roleName, "roleName");
        this.roleName = roleName;
    }

    /**
     * Builds the role with the specified permissions.
     * @param perms the permissions to use.
     * @return the role.
     * @throws IllegalArgumentException if the permissions array is
     * {@code null} or some of its elements are {@code null}.
     */
    public Role can(AddressPermission...perms) {
        requireArrayOfMinLength(0, perms);

        Set<AddressPermission> ps = asStream(perms).collect(toSet());
        return new Role(roleName,
                        ps.contains(Send),
                        ps.contains(Consume),
                        ps.contains(CreateDurableQueue),
                        ps.contains(DeleteDurableQueue),
                        ps.contains(CreateNonDurableQueue),
                        ps.contains(DeleteNonDurableQueue),
                        ps.contains(Manage),
                        ps.contains(Browse));
    }

}

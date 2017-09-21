package kew.providers.artemis.config.security;

import static java.util.stream.Collectors.toSet;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.sequence.Arrayz.array;
import static util.sequence.Arrayz.asStream;

import org.apache.activemq.artemis.core.security.Role;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import util.object.Either;
import util.sequence.Streams;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@RunWith(Theories.class)
public class RolePermissionsBuilderTest {

    private static boolean isPermissionSet(Role r, AddressPermission p) {
        switch (p) {
            case Send:
                return r.isSend();
            case Consume:
                return r.isConsume();
            case CreateDurableQueue:
                return r.isCreateDurableQueue();
            case DeleteDurableQueue:
                return r.isDeleteDurableQueue();
            case CreateNonDurableQueue:
                return r.isCreateNonDurableQueue();
            case DeleteNonDurableQueue:
                return r.isDeleteNonDurableQueue();
            case Manage:
                return r.isManage();
            case Browse:
                return r.isBrowse();
            default:
                throw new IllegalArgumentException("unknown constant: " + p);
        }
    }

    @SuppressWarnings("unchecked")
    private static Either<Void, AddressPermission>[] buildPermSupply() {
        Stream<Either<Void, AddressPermission>> ps =
                Stream.of(AddressPermission.values())
                      .map(Either::right);
        return Streams.concat(ps, Stream.of(Either.left(null)))
                      .toArray(Either[]::new);
    }

    private static AddressPermission[] toPermsArray(
            Either<Void, AddressPermission>[] eps) {
        List<AddressPermission> ps = new ArrayList<>();
        for (Either<Void, AddressPermission> e : eps) {
            if (e.isRight()) {
                ps.add(e.getRight());
            }
        }
        return ps.toArray(new AddressPermission[] {});
    }
    /* So here comes my daily Java WTF. I could've written this method simply
     * as
     *     Stream.of(eps)
     *           .filter(Either::isRight)
     *           .map(Either::right)
     *           .toArray(AddressPermission[]::new);
     *
     * Except that gives you back a fat ArrayStore exception instead of the
     * array!
     */

    @DataPoints
    public static Either<Void, AddressPermission>[] permSupply =
            buildPermSupply();
    /* the idea here is that if we have a right then we set that permission,
     * whereas we do nothing if we have a left.
     */

    @Theory
    public void checkCombinations(
            Either<Void, AddressPermission> p1,
            Either<Void, AddressPermission> p2,
            Either<Void, AddressPermission> p3) {
        AddressPermission[] ps = toPermsArray(array(p1, p2, p3));
        Role target = RolePermissionsBuilder.role("r").can(ps);

        Set<AddressPermission> expectedPerms = asStream(ps).collect(toSet());
        for (AddressPermission p : AddressPermission.values()) {
            boolean hasPerm = isPermissionSet(target, p);
            boolean shouldOfSet = expectedPerms.contains(p);
            assertThat(hasPerm, is(shouldOfSet));
        }
    }
    /* NOTE
     * If this method has as many params as enum values, then it'd check *all*
     * possible combinations, since Theories draws elements from the cartesian
     * product E^n where E = Either<Void, AddressPermission> and n = number of
     * params, 3 in this case. So testing with 8 parameters would be an
     * exhaustive check, except it takes ~ 10 mins on my box---approx 40*10^6
     * combinations!
     */

    @Test
    public void setAllPermissions() {
        AddressPermission[] ps = AddressPermission.values();
        Role target = RolePermissionsBuilder.role("r").can(ps);

        for (AddressPermission p : ps) {
            assertTrue(p.toString(), isPermissionSet(target, p));
        }
    }
    // NB the case of setting no permissions is part of the theory method above.

    @Test (expected = IllegalArgumentException.class)
    public void ctorThrowsIfNullRoleName() {
        new RolePermissionsBuilder(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void ctorThrowsIfEmptyRoleName() {
        new RolePermissionsBuilder("");
    }

}

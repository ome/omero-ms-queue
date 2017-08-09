package util.types;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class UuidStringTest {

    @Test(expected = IllegalArgumentException.class)
    public void ctorThrowsIfNullUuid() {
        new UuidString(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ctorThrowsIfEmptyUuid() {
        new UuidString("");
    }

    @Test
    public void keepIdWhenExplicitlySpecified() {
        String id = "x";
        UuidString x = new UuidString(id);

        assertThat(x.id(), is(x.get()));
        assertThat(x.id(), is(id));
    }

    @Test
    public void generatedUuidsAreUnique() {
        UuidString x = new UuidString();
        UuidString y = new UuidString();

        assertThat(x.id(), is(not(y.id())));
    }

}

package ome.smuggler.core.types;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;


public class ImportIdTest {

    @Test
    public void batchIdSameAsPassedToCtor() {
        ImportBatchId expected = new ImportBatchId();
        ImportId id = new ImportId(expected);

        assertThat(id.batchId(), is(expected));
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullBatchId() {
        new ImportId((ImportBatchId) null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void ctorThrowsIfNullStringId() {
        new ImportId((String) null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void ctorThrowsIfEmptyStringId() {
        new ImportId("");
    }

}

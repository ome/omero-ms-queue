package ome.smuggler.web.imports;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.sequence.Arrayz.array;

import org.junit.Before;
import org.junit.Test;

import ome.smuggler.core.types.OmeroDefault;
import ome.smuggler.web.Error;
import util.object.Either;

public class ImportRequestValidatorTest {

    private ImportRequestValidator validator;
    private ImportRequest minValidInput;
    
    private void assertFailure(ImportRequest input) {
        Either<Error, ImportRequest> outcome = validator.validate(input);
        assertNotNull(outcome);
        assertTrue(outcome.isLeft());
        assertNotNull(outcome.getLeft());
        assertThat(outcome.getLeft().reason, is(not("")));
    }
    
    private void assertSuccess(ImportRequest input) {
        Either<Error, ImportRequest> outcome = validator.validate(input);
        assertNotNull(outcome);
        assertTrue(outcome.isRight());
        assertThat(outcome.getRight(), is(input));
    }
    
    @Before
    public void setup() {
        validator = new ImportRequestValidator();
        minValidInput = Utils.newImportRequest(0);
    }
    
    @Test
    public void failIfNullInput() {
        assertFailure(null);
    }
    
    @Test
    public void failIfMissingEmail() {
        assertFailure(new ImportRequest());
    }
    
    @Test
    public void failIfMissingTarget() {
        ImportRequest r = new ImportRequest();
        r.experimenterEmail = "e@edu";
        
        assertFailure(r);
    }
    
    @Test
    public void failIfMissingOmeroHost() {
        ImportRequest r = new ImportRequest();
        r.experimenterEmail = "e@edu";
        r.targetUri = "/some/file";
        
        assertFailure(r);
    }
    
    @Test
    public void defaultOmeroPort() {
        ImportRequest r = new ImportRequest();
        r.experimenterEmail = "e@edu";
        r.targetUri = "/some/file";
        r.omeroHost = "omero";
        r.sessionKey = "sesh";
        
        assertSuccess(r);
        assertThat(r.omeroPort, 
                   is(OmeroDefault.Port.toString()));
    }
    
    @Test
    public void failIfMissingSessionKey() {
        ImportRequest r = new ImportRequest();
        r.experimenterEmail = "e@edu";
        r.targetUri = "/some/file";
        r.omeroHost = "omero";
        r.omeroPort = "1234";
        
        assertFailure(r);
    }
    
    @Test
    public void succeedIfAllRequiredFieldsSupplied() {
        assertSuccess(minValidInput);
    }
    
    @Test
    public void failIfBothDatasetAndScreenSpecified() {
        minValidInput.datasetId = "1";
        minValidInput.screenId = "2";
        
        assertFailure(minValidInput);
    }
    
    @Test
    public void succeedIfOnlyDatasetSpecified() {
        minValidInput.datasetId = "1";
        
        assertSuccess(minValidInput);
    }
    
    @Test
    public void succeedIfOnlyScreenSpecified() {
        minValidInput.screenId = "1";
        
        assertSuccess(minValidInput);
    }
    
    @Test
    public void failIfInvalidScreenSpecified() {
        minValidInput.screenId = "-1";
        
        assertFailure(minValidInput);
    }
    
    @Test
    public void failIfInvalidDatasetSpecified() {
        minValidInput.datasetId = "x";
        
        assertFailure(minValidInput);
    }
    
    @Test
    public void succeedIfEmptyTextAnnoArray() {
        minValidInput.textAnnotations = new String[][] {};
        
        assertSuccess(minValidInput);
    }
    
    @Test
    public void failIfTextAnnoArrayHasNulls() {
        minValidInput.textAnnotations = new String[][] { array("n1", "v1"), null };
        
        assertFailure(minValidInput);
    }
    
    @Test
    public void succeedIfEmptyAnnoIdArray() {
        minValidInput.annotationIds = array();
        
        assertSuccess(minValidInput);
    }
    
    @Test
    public void failIfAnnoIdArrayHasNulls() {
        minValidInput.annotationIds = array("id1", null);
        
        assertFailure(minValidInput);
    }
    
}

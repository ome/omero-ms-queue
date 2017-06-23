package end2end.web;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.error.Exceptions.runUnchecked;
import static end2end.web.Asserts.*;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import ome.smuggler.core.types.ImportId;
import ome.smuggler.web.imports.ImportController;
import ome.smuggler.web.imports.ImportFailureController;
import ome.smuggler.web.imports.ImportRequest;
import ome.smuggler.web.imports.ImportResponse;


public class VerifyImportTest extends BaseWebTest {
    
    private static ImportRequest buildValidRequestThatWillFail() {
        ImportRequest req = new ImportRequest();
        req.experimenterEmail = "x@y";
        req.targetUri = "my/file";
        req.omeroHost = "h";
        req.omeroPort = "1";
        req.sessionKey = "k";
        
        return req;
    }
    
    private static void assertImportLogContent(
            Optional<String> actual, ImportRequest expected) {
        assertTrue(actual.isPresent());
        
        String importLog = actual.get();
        assertThat(importLog, containsString(expected.targetUri));
        assertThat(importLog, containsString(expected.experimenterEmail));
    }
    
    
    private TaskFileStoreClient<ImportId> statusUpdateClient;
    private TaskFileStoreClient<ImportId> failedLogClient;
    
    @Before
    public void setup() {
        statusUpdateClient = new TaskFileStoreClient<>(httpClient, 
                ImportController.ImportUrl, Asserts::assertNoCaching, 
                ImportId::new);
        failedLogClient = new TaskFileStoreClient<>(httpClient, 
                ImportFailureController.RootPath, 
                Asserts::assertCacheForAsLongAsPossible, 
                ImportId::new);
    }
    
    private ImportId requestImport(ImportRequest req) {
        ResponseEntity<ImportResponse[]> postImportResponse =
                post(url(ImportController.ImportUrl),
                     new ImportRequest[] { req },
                     ImportResponse[].class);
        assert200(postImportResponse);
        return statusUpdateClient.taskIdFromUrl(
                postImportResponse.getBody()[0].statusUri);
    }
    
    private void canGetStatusUpdate(ImportId taskId, ImportRequest requested) {
        Optional<String> body = statusUpdateClient.download(taskId);  // (*)
        assertImportLogContent(body, requested);
    }
    // (*) client asserts 200, no caching, plain text.
    
    private void noMoreImportStatusUpdatesAfterLogRetentionPeriod(ImportId taskId) {
        Optional<String> body = statusUpdateClient.download(taskId); // asserts 404
        assertFalse(body.isPresent());  // ==> log was garbage collected
    }
    
    private void canGetFailedImportLog(ImportId taskId) {
        assertTrue(failedLogClient.exists(taskId));
    }
    
    private void canDownloadFailedLog(ImportId taskId, ImportRequest requested) {
        Optional<String> body = failedLogClient.download(taskId);  // (*)
        assertImportLogContent(body, requested);
    }
    // (*) client asserts 200, caching forever, plain text.
    
    private void cannotDownloadFailedLog(ImportId taskId) {
        Optional<String> body = failedLogClient.download(taskId);  // asserts 404
        assertFalse(body.isPresent());
    }
    
    private void canStopTrackingFailedLog(ImportId taskId) {
        failedLogClient.delete(taskId);  // asserts 204
    }
    
    private void failedLogNoLongerTracked(ImportId taskId) {
        assertFalse(failedLogClient.exists(taskId));
    }
    
    private void waitUntilPastLogRetentionPeriod() {
        long millis = config.importConfig.logRetentionPeriod()
                     .plusSeconds(60).toMillis();
        runUnchecked(() -> Thread.sleep(millis));
    }
    
    @Test
    public void failedImportWorkflow() {
        ImportRequest doomedImportRequest = buildValidRequestThatWillFail();
        ImportId taskId = requestImport(doomedImportRequest);
        canGetStatusUpdate(taskId, doomedImportRequest);
        
        waitUntilPastLogRetentionPeriod();  // (1)
        noMoreImportStatusUpdatesAfterLogRetentionPeriod(taskId);
        
        canGetFailedImportLog(taskId);  // (2)
        canDownloadFailedLog(taskId, doomedImportRequest);
        canStopTrackingFailedLog(taskId);
        cannotDownloadFailedLog(taskId);
        failedLogNoLongerTracked(taskId);  // (3)
    }
    /* NOTES.
     * 1. Assumes the duration in config.importConfig is the same as that used 
     * by Spring; Config class takes care of that. 
     * 2. Assumes the import config used by Spring has no retry intervals so 
     * that the failed log is immediately available after the first failure;
     * this is why the BaseWebTest sets the Dev profile.
     * 3. Note that there may be more than one failed log. In fact, tests are
     * run concurrently and so EnqueueImportTest::postValidRequest may result
     * in a failed log being produced just before we ask Smuggler to list all
     * failed logs. So we can't just assume and check the returned list is [].
     */
}

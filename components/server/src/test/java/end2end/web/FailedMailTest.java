package end2end.web;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import ome.smuggler.core.types.MailId;
import ome.smuggler.web.mail.MailFailureController;

public class FailedMailTest extends BaseWebTest {

    private TaskFileStoreClient<MailId> client;
    
    @Before
    public void setup() {
        client = new TaskFileStoreClient<>(
                httpClient, MailFailureController.RootPath,
                Asserts::assertCacheForAsLongAsPossible, MailId::new);
    }
    
    @Test
    public void noDownloadIfNonExistentId() {
        MailId taskId = new MailId();
        Optional<String> file = client.download(taskId);  // asserts
        
        assertFalse(file.isPresent());
    }
    
    @Test
    public void deleteWithNonExistentIdHasNoEffect() {
        MailId taskId = new MailId();
        client.delete(taskId);  // asserts
    }
    
    @Test
    public void listDownloadDeleteWorkflow() {
        MailId taskId = new MailId();
        String fileContent = "***";
        config.failedMailStore.add(taskId, fileContent);
        
        assertTrue(client.exists(taskId));  // calls loadTaskIds which asserts
        
        Optional<String> downloadedContent = client.download(taskId);
        assertTrue(downloadedContent.isPresent());
        assertThat(downloadedContent.get(), is(fileContent));
        
        client.delete(taskId);  // asserts
        assertFalse(client.exists(taskId));  // calls loadTaskIds which asserts
    }
    
}

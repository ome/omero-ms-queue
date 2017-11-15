package end2end.web;

import static end2end.web.BaseWebTest.url;
import static end2end.web.Asserts.assert200;
import static end2end.web.Asserts.assert200andReturnTrueOr404andReturnFalse;
import static end2end.web.Asserts.assert204;
import static end2end.web.Asserts.assertPlainText;
import static org.junit.Assert.assertNotNull;

import java.net.URI;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import util.object.Identifiable;

public class TaskFileStoreClient<T extends Identifiable> {

    protected final RestTemplate httpClient;
    protected final String rootPath;
    protected final Consumer<ResponseEntity<?>> assertCachingStrategy;
    protected final Function<String, T> newTaskId;
    
    private URI listUrl() {
        return url(rootPath);
    }
    
    private URI taskUrl(T task) {
        return url(rootPath, task.id());
    }
    
    private String[] listTaskFileUrls() {
        ResponseEntity<String[]> response = 
                httpClient.getForEntity(listUrl(), String[].class);
        assert200(response);
        
        String[] ids = response.getBody();
        assertNotNull(ids);
        
        return ids;
    }
    
    public TaskFileStoreClient(RestTemplate httpClient, String rootPath,
            Consumer<ResponseEntity<?>> assertCachingStrategy,
            Function<String, T> newTaskId) {
        this.httpClient = httpClient;
        this.rootPath = rootPath;
        this.assertCachingStrategy = assertCachingStrategy;
        this.newTaskId = newTaskId;
    }
    
    public T taskIdFromUrl(String path) {
        String id = Paths.get(path).getFileName().toString();
        return newTaskId.apply(id);
    }
    
    public Stream<T> loadTaskIds() {
        return Stream.of(listTaskFileUrls()).map(this::taskIdFromUrl);
    }
    
    public boolean exists(T taskId) {
        return loadTaskIds().anyMatch(taskId::equals);
    }
    
    public Optional<String> download(T taskId) {
        ResponseEntity<String> response = 
                httpClient.getForEntity(taskUrl(taskId), String.class);
        if (assert200andReturnTrueOr404andReturnFalse(response)) {
            assertCachingStrategy.accept(response);
            assertPlainText(response);
            
            String fileContents = response.getBody();
            return Optional.of(fileContents);
        }
        else {
            return Optional.empty();
        }
    }
    
    public void delete(T taskId) {
        RequestEntity<Object> request = 
                new RequestEntity<>(HttpMethod.DELETE, taskUrl(taskId));
        ResponseEntity<Object> response = 
                httpClient.exchange(request, Object.class);
        assert204(response);
    }
    
}

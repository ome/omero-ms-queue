package ome.smuggler.web;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static util.spring.http.ResponseEntities._204;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

import ome.smuggler.core.service.file.TaskFileStore;
import util.object.Identifiable;
import util.servlet.http.Caches;

/**
 * Provides the means to make the functionality of a {@link TaskFileStore} 
 * available to web clients.
 */
public abstract class TaskFileStoreAdapter<T extends Identifiable> {
    
    private static final String TaskIdPathVar = "taskId";
    
    
    private String toUrlString(T taskId) {
        URI rootPath = URI.create(rootPath()); 
        return UriComponentsBuilder.newInstance()
                                   .path(rootPath.getPath())
                                   .path("/")
                                   .path(taskId.id())
                                   .toUriString();
    }
    
    /**
     * @return the underlying task file store.
     */
    protected abstract TaskFileStore<T> service();
    
    /**
     * @return the base web path from which the files will be served.
     */
    protected abstract String rootPath();
    
    /**
     * @return a function to create a task ID from its string representation.
     */
    protected abstract Function<String, T> taskIdFromString();
    
    /**
     * @return the content type of the files in the store; defaults to plain
     * text.
     */
    protected MediaType filesContentType() {
        return MediaType.TEXT_PLAIN;
    }
    
    /**
     * @return the HTTP caching strategy to use when streaming files to the 
     * client; defaults to caching forever.
     * @see Caches 
     */
    protected Consumer<HttpServletResponse> cacheStrategy() {
        return Caches::cacheForAsLongAsPossible;
    }
    
    /**
     * @return the URL root paths to access each of the files currently in the
     * store. 
     */
    @RequestMapping(method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String[] listTaskFileUrlPaths() {
        return service().listTaskIds()
                        .map(this::toUrlString)
                        .toArray(String[]::new);
    }
    
    /**
     * Looks up the file associated to a task using the task ID and streams that
     * file to the client if found; otherwise outputs a 404 to the client.
     * @param taskId the ID of the task associated to the file.
     * @param response the response to use to stream the file.
     * @return dummy entity to make this work with Spring MVC.
     * @throws IOException If an error occurs while reading and streaming the
     * file.
     */
    @RequestMapping(method = GET, value = "{" + TaskIdPathVar + "}") 
    public ResponseEntity<String> streamFileOr404(
            @PathVariable(value=TaskIdPathVar) String taskId, 
            HttpServletResponse response) throws IOException {
        Path file = service().pathFor(taskIdFromString().apply(taskId)); 
        FileStreamer streamer = new FileStreamer(file, filesContentType(), 
                                                 cacheStrategy());
        return streamer.streamOr404(response);
    }
    
    /**
     * Removes the file associated to a task if it is in the store; does nothing
     * otherwise.
     * @param taskId the ID of the task associated to the file.
     * @return a 204 response to the client.
     */
    @RequestMapping(method = DELETE, value = "{" + TaskIdPathVar + "}") 
    public ResponseEntity<Void> deleteFile(
            @PathVariable(value=TaskIdPathVar) String taskId) {
        service().remove(taskIdFromString().apply(taskId)); 
        return _204();
    }
    
}

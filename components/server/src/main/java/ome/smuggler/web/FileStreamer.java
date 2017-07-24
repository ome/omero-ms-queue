package ome.smuggler.web;

import static java.util.Objects.requireNonNull;
import static util.spring.http.ResponseEntities._404;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import util.io.FileOps;
import util.types.Nat;

/**
 * Streams a file to the HTTP response body or sends a 404 if the file does not
 * exist.
 */
public class FileStreamer {

    private final Path content;
    private final MediaType contentType;
    private final Consumer<HttpServletResponse> cacheStrategy;
    
    /**
     * Creates a new instance to stream the specified file.
     * @param content path to the file to stream.
     * @param contentType the content type to set in the response.
     * @param cacheStrategy sets cache directives in the response.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public FileStreamer(Path content, MediaType contentType, 
                        Consumer<HttpServletResponse> cacheStrategy) {
        requireNonNull(content, "content");
        requireNonNull(contentType, "contentType");
        requireNonNull(cacheStrategy, "cacheStrategy");
        
        this.content = content;
        this.contentType = contentType;
        this.cacheStrategy = cacheStrategy;
    }
    
    private void stream(HttpServletResponse response) throws IOException {
        Nat contentSize = FileOps.byteLength(content);  
        
        if (contentSize.get() <= Integer.MAX_VALUE) {
            response.setContentLength(contentSize.get().intValue());
        }  // (1)
        
        response.setContentType(contentType.toString());
        cacheStrategy.accept(response);
        
        ServletOutputStream out = response.getOutputStream();
        FileOps.transfer(content, contentSize, out);  // (2)
    }
    /* NOTES.
     * 1. Else can't set; will result in chunked transfer encoding.
     * To see this, use a 200M file without setting the content-length; then the 
     * servlet container splits the content into 16KiB chunks (HEX size = 4000).
     * 2. The file is possibly being written to, so we can't just use Files.copy
     * as it may write more bytes than we declared for the content-length.
     */
    
    /**
     * Streams the file to the client or sends back a 404 if the file does not
     * exist.
     * @param response the response to send to the client.
     * @return a suitable entity to Spring.
     * @throws IOException if an I/O error occurs.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public ResponseEntity<String> streamOr404(HttpServletResponse response) 
            throws IOException {
        requireNonNull(response, "response");
        
        if(Files.exists(content)) {
            stream(response);
            return null;   // (*)
        } else {
            return _404(); // (*)
        }
    }
    /* (*) What? Needed if we want to return a 404.
     * Initially this method returned void and if the file was not found I'd use
     * 
     *  response.sendError(HttpStatus.NOT_FOUND.value(), 
     *                     HttpStatus.NOT_FOUND.getReasonPhrase());
     * 
     * If the file was found, then its content would be streamed as expected,
     * but if the file wasn't there, sendError would have no effect and Spring
     * MVC would override it with a 406 response, possibly caused by the fact
     * a HTTP Converter was selected to handle the response body conversion.
     * So I changed the controller annotation from @RestController to plain
     * @Controller (i.e. no @ResponseBody) and tried to send different accept
     * headers in the request ('* / *', 'text / plain', '* / *, text / plain')
     * but would still get a fat 406! 
     */
}

package util.servlet.http;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.function.Consumer;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Wraps an {@link HttpServletResponse} to call a response consumer just before 
 * the moment past which the response character encoding cannot be set anymore.
 */
public class ResponseCharEncodingWrapper extends HttpServletResponseWrapper {
    
    private final HttpServletResponse target;
    private final Consumer<HttpServletResponse> hook;
    
    /**
     * Creates a new instance.
     * @param target the request.
     * @param hook the consumer to call. 
     * @throws IllegalArgumentException if the response is {@code null}.
     * @throws NullPointerException if the hook is {@code null}.
     */
    public ResponseCharEncodingWrapper(
            HttpServletResponse target, Consumer<HttpServletResponse> hook) {
        super(target);
        requireNonNull(hook, "hook");
        
        this.target = target;
        this.hook = hook;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        hook.accept(target);
        return super.getOutputStream();
    }
    
    @Override
    public PrintWriter getWriter() throws IOException {
        hook.accept(target);
        return super.getWriter();
    }
    
}

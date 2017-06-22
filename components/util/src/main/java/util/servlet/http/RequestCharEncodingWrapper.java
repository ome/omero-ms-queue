package util.servlet.http;

import static java.util.Objects.requireNonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.function.Consumer;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Wraps an {@link HttpServletRequest} to call a request consumer just before 
 * the moment past which the request character encoding cannot be set anymore.
 */
public class RequestCharEncodingWrapper extends HttpServletRequestWrapper {
    
    private final HttpServletRequest target;
    private final Consumer<HttpServletRequest> hook;
    
    /**
     * Creates a new instance.
     * @param target the request.
     * @param hook the consumer to call.
     * @throws IllegalArgumentException if the request is {@code null}.
     * @throws NullPointerException if the hook is {@code null}.
     */
    public RequestCharEncodingWrapper(
            HttpServletRequest target, Consumer<HttpServletRequest> hook) {
        super(target);
        requireNonNull(hook, "hook");
        
        this.target = target;
        this.hook = hook;
    }
    
    @Override
    public String getParameter(String name) {
        hook.accept(target);
        return super.getParameter(name);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        hook.accept(target);
        return super.getParameterNames();
    }

    @Override
    public String[] getParameterValues(String name) {
        hook.accept(target);
        return super.getParameterValues(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        hook.accept(target);
        return super.getParameterMap();
    }
    
    @Override 
    public ServletInputStream getInputStream() throws IOException {
        hook.accept(target);
        return super.getInputStream();
    }
    
    @Override
    public BufferedReader getReader() throws IOException {
        hook.accept(target);
        return super.getReader();
    }
    
}

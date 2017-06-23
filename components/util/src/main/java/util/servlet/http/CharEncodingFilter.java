package util.servlet.http;

import static java.util.Objects.requireNonNull;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.object.Pair;
import util.servlet.ServletCharEncoding;

/**
 * Base class for filters that set request/response character encoding.
 * @see RequestCharEncodingFilter
 * @see ResponseCharEncodingFilter
 */
public abstract class CharEncodingFilter implements Filter {
    
    /* NB this is how the various possible cases are handled.
     * 
     *   E = char Encoding is set; !E = not set;
     *   T = content type is Text (i.e. any text/*) but no charset specified;
     *   O = content type Other than text/* (e.g. application/jason) or no 
     *       content type at all.
     *       
     * Request !E O => set default encoding (1)
     *          E O => do nothing
     *         !E T => set default encoding
     *          E T => do nothing
     * 
     * Response !E O => set default encoding (2)
     *           E O => do nothing
     *          !E T => set default encoding (3)
     *           E T => do nothing
     *                    
     * (1) setting the encoding should have no effect on getInputStream but the
     *     Reader returned by getReader should be created with the default 
     *     encoding that was set; no harm can be done.
     * (2) same argument as above but for getOutputStream and getWriter.
     * (3) even if the response is written through getOutputStream, it will have
     *     the default encoding set which fits in with our intents as the 
     *     content type is text so we assume the application is going to write
     *     in the default encoding, otherwise they shouldn't have used this
     *     filter.
     *     
     * So we only have one condition to check: char encoding not set.
     * To set the default encoding, we going to wait until the very last moment 
     * after which it's no longer possible to set the encoding. (See JavaDoc of
     * setContentType and setEncoding.)
     */

    /**
     * Creates a new request filter to default character encoding to UTF-8. 
     * @return a new filter to add UTF-8 default encoding.
     */
    public static RequestCharEncodingFilter Utf8Request() {
        return new RequestCharEncodingFilter(ServletCharEncoding.Utf8());
    }
    
    /**
     * Creates a new response filter to default character encoding to UTF-8. 
     * @return a new filter to add UTF-8 default encoding.
     */
    public static ResponseCharEncodingFilter Utf8Response() {
        return new ResponseCharEncodingFilter(ServletCharEncoding.Utf8());
    }
    
    protected final ServletCharEncoding defaultEncoder;
    
    protected CharEncodingFilter(ServletCharEncoding defaultEncoder) {
        requireNonNull(defaultEncoder, "defaultEncoder");
        this.defaultEncoder = defaultEncoder;
    }
    
    protected abstract Pair<ServletRequest, ServletResponse> wrap(
            HttpServletRequest req, HttpServletResponse res);
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest
            && response instanceof HttpServletResponse) {
            Pair<ServletRequest, ServletResponse> wrapped = wrap(
                    (HttpServletRequest) request,
                    (HttpServletResponse) response);
            
            request = wrapped.fst();
            response = wrapped.snd();
        }
        chain.doFilter(request, response);
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    public void destroy() { }
    
}

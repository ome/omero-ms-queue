package util.servlet.http;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.object.Pair;
import util.servlet.ServletCharEncoding;

/**
 * <p>
 * Sets character encoding for servlets that write characters to the response
 * if none has been specified.
 * This filter is useful when most or all web components in the application
 * handle text in a given character encoding only (e.g. UTF-8); using this 
 * filter explicit setting of the character encoding in each component can 
 * then be avoided.
 * </p>
 * <p>Note that in order to avoid interference (as much as possible) with other
 * components in the HTTP processing pipeline, this filter delays the setting of
 * the character encoding until the very last moment it is possible to do it. 
 * (According to the servlet spec, it is just before the response body is 
 * written.) In fact, a response producer downstream may make a decision as to
 * what encoding to use based on the absence of the character encoding, which 
 * is why this filter holds off setting it. 
 * </p>
 * @see RequestCharEncodingFilter
 */
public class ResponseCharEncodingFilter extends CharEncodingFilter {
    
    /**
     * Creates a new instance to set the given default encoding.
     * @param defaultEncoder the encoder that can set the default encoding.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public ResponseCharEncodingFilter(ServletCharEncoding defaultEncoder) {
        super(defaultEncoder);
    }
    
    @Override
    protected Pair<ServletRequest, ServletResponse> wrap(
            HttpServletRequest request, HttpServletResponse response) {
        ResponseCharEncodingWrapper wrapper = new ResponseCharEncodingWrapper(
                response, 
                defaultEncoder::setEncodingIfAbsent);
        return new Pair<>(request, wrapper);
    }
    
}

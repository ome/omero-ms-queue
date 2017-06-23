package util.servlet;

import static java.util.Objects.requireNonNull;
import static util.error.Exceptions.unchecked;
import static util.string.Strings.isNullOrEmpty;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Convenience class to set default character encodings on servlet requests and
 * responses.
 */
public class ServletCharEncoding {

    private static boolean setEncodingIfAbsent(Supplier<String> getter, 
            Consumer<String> setter, String defaultEncoding) {
        String givenEncoding = getter.get();
        if (isNullOrEmpty(givenEncoding) 
            || givenEncoding.trim().length() == 0) {
            setter.accept(defaultEncoding);
            return true;
        }
        return false;
    }
    
    /**
     * Sets the request character encoding if the request doesn't have one.
     * @param r the request.
     * @param defaultEncoding value to set if absent. 
     * @return {@code true} if the request had no encoding and the given default
     * was set; {@code false} otherwise.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static boolean setEncodingIfAbsent(ServletRequest r, 
            Charset defaultEncoding) {
        requireNonNull(r, "request");
        requireNonNull(defaultEncoding, "defaultEncoding");
        
        return setEncodingIfAbsent(r::getCharacterEncoding, 
                                       unchecked(r::setCharacterEncoding),
                                       defaultEncoding.name());
    }
    
    /**
     * Sets the response character encoding if the response doesn't have one.
     * @param r the response.
     * @param defaultEncoding value to set if absent. 
     * @return {@code true} if the response had no encoding and the given 
     * default was set; {@code false} otherwise.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static boolean setEncodingIfAbsent(ServletResponse r, 
            Charset defaultEncoding) {
        requireNonNull(r, "response");
        requireNonNull(defaultEncoding, "defaultEncoding");
        
        return setEncodingIfAbsent(r::getCharacterEncoding, 
                                   r::setCharacterEncoding,
                                   defaultEncoding.name());
    }
    
    /**
     * Creates a new instance to default character encoding to UTF-8. 
     * @return a new instance for UTF-8 default encoding.
     */
    public static ServletCharEncoding Utf8() {
        return new ServletCharEncoding(StandardCharsets.UTF_8);
    }
    
    private final Charset defaultEncoding;
    
    /**
     * Creates a new instance to set the specified encoding if none is specified
     * in the request/response or by the servlet.
     * @param defaultEncoding the default encoding to use if none is present.
     */
    public ServletCharEncoding(Charset defaultEncoding) {
        requireNonNull(defaultEncoding, "defaultEncoding");
        
        this.defaultEncoding = defaultEncoding;
    }
    
    /**
     * Sets the request character encoding if the request doesn't have one.
     * @param r the request.
     * @return {@code true} if the request had no encoding and the given default
     * was set; {@code false} otherwise.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public boolean setEncodingIfAbsent(ServletRequest r) {
        return setEncodingIfAbsent(r, defaultEncoding);
    }
    
    /**
     * Sets the response character encoding if the response doesn't have one.
     * @param r the response.
     * @return {@code true} if the response had no encoding and the given 
     * default was set; {@code false} otherwise.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public boolean setEncodingIfAbsent(ServletResponse r) {
        return setEncodingIfAbsent(r, defaultEncoding);
    }
    
}

package util.spring.http;

import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * Helper methods to deal with media types.
 */
public class MediaTypes {
    
    /**
     * Attempts to parse the given comma-separated list of media types.
     * @param csv the string to parse.
     * @return the parsed media types; if a parse error occurs, the returned
     * stream will be empty.
     */
    public static Stream<MediaType> tryParseMediaTypes(String csv) {
        try {
            return MediaType.parseMediaTypes(csv).stream();
        } catch (IllegalArgumentException parseError) {
            return Stream.empty();
        }
    }
    
    /**
     * Attempts to parse the given comma-separated list of media types and find
     * which of them are compatible with the specified one to match.
     * @param toMatch the media type to match.
     * @param csv the string to parse.
     * @return the parsed media types that are compatible; the stream may be
     * empty if a parse error occurred or no compatible types were found.
     */
    public static Stream<MediaType> findCompatibleMediaTypes(
            MediaType toMatch, String csv) {
        if (toMatch == null) return Stream.empty();
        return tryParseMediaTypes(csv).filter(toMatch::isCompatibleWith);
    }
    
    /**
     * Does the given request has an Accept header containing any media type
     * compatible to the one to match?
     * @param toMatch the media type to match.
     * @param request the request.
     * @return {@code true} for yes, {@code false} for no.
     */
    public static boolean acceptsMediaType(
            MediaType toMatch, HttpServletRequest request) {
        String acceptHeaderValues = request.getHeader(HttpHeaders.ACCEPT);
        return 
            findCompatibleMediaTypes(toMatch, acceptHeaderValues).count() > 0;
    }
    
}

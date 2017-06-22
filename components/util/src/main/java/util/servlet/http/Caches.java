package util.servlet.http;

import static java.util.Objects.requireNonNull;

import java.util.StringJoiner;

import javax.servlet.http.HttpServletResponse;

/**
 * Helper methods for resource caching strategies.
 */
public class Caches {
    
    public static final String CacheControl = "Cache-Control";
    public static final String Pragma = "Pragma";
    public static final String Expires = "Expires";
    public static final String NoStore = "no-store"; 
    public static final String NoCache = "no-cache";
    public static final String MustRevalidate = "must-revalidate";
    public static String MaxAge(int seconds) { 
        return "max-age=" + seconds;
    }
    
    /**
     * Sets headers so that the response is never cached.
     * @param response the response on which to set the headers.
     * @throws NullPointerException if the argument is {@code null}. 
     */
    public static void doNotCache(HttpServletResponse response) {
        requireNonNull(response, "response");
        
        response.setHeader(CacheControl, new StringJoiner(", ")
                                        .add(NoStore)
                                        .add(NoCache)
                                        .add(MustRevalidate)
                                        .toString());
        response.setHeader(Pragma, NoCache);
        response.setIntHeader(Expires, 0);
    }
    /* See:
     * - http://stackoverflow.com/questions/49547/making-sure-a-web-page-is-not-cached-across-all-browsers
     * - https://developers.google.com/web/fundamentals/performance/optimizing-content-efficiency/http-caching?hl=en
     */
    
    /**
     * Sets headers so that the response is cached for as long as possible.
     * The HTTP standard advises this should be at most one year in the future.
     * @param response the response on which to set the headers.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static void cacheForAsLongAsPossible(HttpServletResponse response) {
        requireNonNull(response, "response");
        
        response.setHeader(CacheControl, MaxAge(60*60*24*365));
    }
    /* See:
     * - http://stackoverflow.com/questions/7071763/max-value-for-cache-control-header-in-http
     * - http://stackoverflow.com/questions/5799906/what-s-the-difference-between-expires-and-cache-control-headers
     * - https://developers.google.com/web/fundamentals/performance/optimizing-content-efficiency/http-caching?hl=en
     */
    
}

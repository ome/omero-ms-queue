package util.servlet.http;

import static org.mockito.Mockito.*;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

public class CachesTest {

    private HttpServletResponse response;
    
    @Before
    public void setup() {
        response = mock(HttpServletResponse.class);
    }
    
    @Test (expected = NullPointerException.class)
    public void doNotCacheThrowsIfNullArg() {
        Caches.doNotCache(null);
    }
    
    @Test (expected = NullPointerException.class)
    public void cacheForAsLongAsPossibleThrowsIfNullArg() {
        Caches.cacheForAsLongAsPossible(null);
    }
    
    @Test
    public void neverCache() {
        Caches.doNotCache(response);
        
        verify(response).setHeader("Cache-Control", 
                                   "no-store, no-cache, must-revalidate");
        verify(response).setHeader("Pragma", "no-cache");
        verify(response).setIntHeader("Expires", 0);
    }
    
    @Test
    public void cacheForAsLongAsPossible() {
        Caches.cacheForAsLongAsPossible(response);
        
        int oneYearInSeconds = 60*60*24*365;
        verify(response).setHeader("Cache-Control", 
                                   "max-age=" + oneYearInSeconds);
    }
    
}

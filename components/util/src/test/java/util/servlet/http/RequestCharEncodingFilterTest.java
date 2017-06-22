package util.servlet.http;

import static org.mockito.Mockito.*;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;


public class RequestCharEncodingFilterTest {

    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private FilterChain chain;
    private RequestCharEncodingFilter target;
    
    @Before
    public void setup() {
        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
        chain = (req, res) -> {
            req.getReader();
            res.getWriter();
        };
        target = CharEncodingFilter.Utf8Request();
    }
    
    @Test(expected = NullPointerException.class)
    public void throwIfNullCtorArg() {
        new RequestCharEncodingFilter(null);
    }
    
    @Test
    public void doNothingIfNotHttp() throws Exception {
        ServletRequest nonHttpRequest = mock(ServletRequest.class);
        target.doFilter(nonHttpRequest, mockResponse, chain);
        
        verify(nonHttpRequest, never()).setCharacterEncoding(anyString());
        verify(mockResponse, never()).setCharacterEncoding(anyString());
    }
    
    @Test
    public void setEncodingIfHttp() throws Exception {
        target.doFilter(mockRequest, mockResponse, chain);
        
        verify(mockRequest).setCharacterEncoding(anyString());
        verify(mockResponse, never()).setCharacterEncoding(anyString());
    }

}

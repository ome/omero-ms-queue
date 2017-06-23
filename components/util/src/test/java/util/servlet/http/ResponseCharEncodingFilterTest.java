package util.servlet.http;

import static org.mockito.Mockito.*;

import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;


public class ResponseCharEncodingFilterTest {

    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private FilterChain chain;
    private ResponseCharEncodingFilter target;
    
    @Before
    public void setup() {
        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
        chain = (req, res) -> {
            req.getReader();
            res.getWriter();
        };
        target = CharEncodingFilter.Utf8Response();
    }
    
    @Test(expected = NullPointerException.class)
    public void throwIfNullCtorArg() {
        new ResponseCharEncodingFilter(null);
    }
    
    @Test
    public void doNothingIfNotHttp() throws Exception {
        ServletResponse nonHttpResponse = mock(ServletResponse.class);
        target.doFilter(mockRequest, nonHttpResponse, chain);
        
        verify(mockRequest, never()).setCharacterEncoding(anyString());
        verify(nonHttpResponse, never()).setCharacterEncoding(anyString());
    }
    
    @Test
    public void setEncodingIfHttp() throws Exception {
        target.doFilter(mockRequest, mockResponse, chain);
        
        verify(mockRequest, never()).setCharacterEncoding(anyString());
        verify(mockResponse).setCharacterEncoding(anyString());
    }
    
}

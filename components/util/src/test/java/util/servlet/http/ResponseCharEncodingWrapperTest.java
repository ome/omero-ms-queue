package util.servlet.http;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.io.IOException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import util.lambda.ConsumerE;


public class ResponseCharEncodingWrapperTest {
    
    private HttpServletResponse mockResponse;
    private boolean hookCalled;
    
    private ResponseCharEncodingWrapper newWrapper(
            ConsumerE<HttpServletResponse> mockMethodCall) {
        return new ResponseCharEncodingWrapper(
                mockResponse, 
                x -> {
                    hookCalled = true;
                    HttpServletResponse target = verify(mockResponse, never());
                    mockMethodCall.accept(target);
                });
    }
    
    @Before
    public void setup() {
        mockResponse = mock(HttpServletResponse.class);
        hookCalled = false;
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void throwIfNullCtorRequestArg() {
        new ResponseCharEncodingWrapper(null, x -> {});
    }
    
    @Test(expected = NullPointerException.class)
    public void throwIfNullCtorHookArg() {
        new ResponseCharEncodingWrapper(mockResponse, null);
    }
    
    @Test
    public void getOutputStreamTriggersHook() throws IOException {
        ResponseCharEncodingWrapper target = newWrapper(ServletResponse::getOutputStream);
        target.getOutputStream();
        
        assertTrue(hookCalled);
        verify(mockResponse, times(1)).getOutputStream();
    }
    
    @Test
    public void getWriterTriggersHook() throws IOException {
        ResponseCharEncodingWrapper target = newWrapper(ServletResponse::getWriter);
        target.getWriter();
        
        assertTrue(hookCalled);
        verify(mockResponse, times(1)).getWriter();
    }
    
}

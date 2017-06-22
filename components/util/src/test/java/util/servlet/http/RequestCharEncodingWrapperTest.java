package util.servlet.http;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.io.IOException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;

import util.lambda.ConsumerE;


public class RequestCharEncodingWrapperTest {
    
    private HttpServletRequest mockRequest;
    private boolean hookCalled;
    
    private RequestCharEncodingWrapper newWrapper(
            ConsumerE<HttpServletRequest> mockMethodCall) {
        return new RequestCharEncodingWrapper(
                mockRequest, 
                x -> {
                    hookCalled = true;
                    HttpServletRequest target = verify(mockRequest, never());
                    mockMethodCall.accept(target);
                });
    }
    
    @Before
    public void setup() {
        mockRequest = mock(HttpServletRequest.class);
        hookCalled = false;
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void throwIfNullCtorRequestArg() {
        new RequestCharEncodingWrapper(null, x -> {});
    }
    
    @Test(expected = NullPointerException.class)
    public void throwIfNullCtorHookArg() {
        new RequestCharEncodingWrapper(mockRequest, null);
    }
    
    @Test
    public void getParameterTriggersHook() {
        RequestCharEncodingWrapper target = newWrapper(r -> r.getParameter(""));
        target.getParameter("");
        
        assertTrue(hookCalled);
        verify(mockRequest, times(1)).getParameter("");
    }
    
    @Test
    public void getParameterNamesTriggersHook() {
        RequestCharEncodingWrapper target = newWrapper(ServletRequest::getParameterNames);
        target.getParameterNames();
        
        assertTrue(hookCalled);
        verify(mockRequest, times(1)).getParameterNames();
    }
    
    @Test
    public void getParameterValuesTriggersHook() {
        RequestCharEncodingWrapper target = newWrapper(r -> r.getParameterValues(""));
        target.getParameterValues("");
        
        assertTrue(hookCalled);
        verify(mockRequest, times(1)).getParameterValues("");
    }
    
    @Test
    public void getParameterMapTriggersHook() {
        RequestCharEncodingWrapper target = newWrapper(ServletRequest::getParameterMap);
        target.getParameterMap();
        
        assertTrue(hookCalled);
        verify(mockRequest, times(1)).getParameterMap();
    }
    
    @Test
    public void getInputStreamTriggersHook() throws IOException {
        RequestCharEncodingWrapper target = newWrapper(ServletRequest::getInputStream);
        target.getInputStream();
        
        assertTrue(hookCalled);
        verify(mockRequest, times(1)).getInputStream();
    }
    
    @Test
    public void getReaderTriggersHook() throws IOException {
        RequestCharEncodingWrapper target = newWrapper(ServletRequest::getReader);
        target.getReader();
        
        assertTrue(hookCalled);
        verify(mockRequest, times(1)).getReader();
    }
    
}

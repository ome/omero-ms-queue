package util.servlet;

import static org.mockito.Mockito.*;

import java.nio.charset.StandardCharsets;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.junit.Before;
import org.junit.Test;


public class ServletCharEncodingTest {

    private ServletRequest mockRequest;
    private ServletResponse mockResponse;
    
    @Before
    public void setup() {
        mockRequest = mock(ServletRequest.class);
        mockResponse = mock(ServletResponse.class);    
    }
    
    @Test(expected = NullPointerException.class)
    public void throwIfNullCtorArg() {
        new ServletCharEncoding(null);
    }
    
    @Test(expected = NullPointerException.class)
    public void setEncodingForRequestThrowsIfNullArg() {
        ServletCharEncoding.Utf8().setEncodingIfAbsent((ServletRequest) null);
    }
    
    @Test(expected = NullPointerException.class)
    public void setEncodingForResponseThrowsIfNullArg() {
        ServletCharEncoding.Utf8().setEncodingIfAbsent((ServletResponse) null);
    }
    
    private void verifyRequestWhen(boolean validEncoding, String charEncoding) 
            throws Exception {
        when(mockRequest.getCharacterEncoding()).thenReturn(charEncoding);
        
        ServletCharEncoding.Utf8().setEncodingIfAbsent(mockRequest);
        
        if (validEncoding) {
            verify(mockRequest, never()).setCharacterEncoding(charEncoding);    
        }
        else {
            verify(mockRequest)
            .setCharacterEncoding(StandardCharsets.UTF_8.name());
        }
    }
    
    @Test
    public void setEncodingForRequestWhenNull() throws Exception {
        verifyRequestWhen(false, null);
    }
    
    @Test
    public void setEncodingForRequestWhenEmpty() throws Exception {
        verifyRequestWhen(false, "");
    }
    
    @Test
    public void setEncodingForRequestWhenWhiteSpace() throws Exception {
        verifyRequestWhen(false, " ");
    }
    
    @Test
    public void setEncodingForRequestWhenPresent() throws Exception {
        verifyRequestWhen(true, "xxx");
    }
    
    private void verifyResponseWhen(boolean validEncoding, String charEncoding) 
            throws Exception {
        when(mockResponse.getCharacterEncoding()).thenReturn(charEncoding);
        
        ServletCharEncoding.Utf8().setEncodingIfAbsent(mockResponse);
        
        if (validEncoding) {
            verify(mockResponse, never()).setCharacterEncoding(charEncoding);    
        }
        else {
            verify(mockResponse)
            .setCharacterEncoding(StandardCharsets.UTF_8.name());
        }
    }
    
    @Test
    public void setEncodingForResponseWhenNull() throws Exception {
        verifyResponseWhen(false, null);
    }
    
    @Test
    public void setEncodingForResponseWhenEmpty() throws Exception {
        verifyResponseWhen(false, "");
    }
    
    @Test
    public void setEncodingForResponseWhenWhiteSpace() throws Exception {
        verifyResponseWhen(false, " ");
    }
    
    @Test
    public void setEncodingForResponseWhenPresent() throws Exception {
        verifyResponseWhen(true, "xxx");
    }
    
}

package util.error;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static util.error.Exceptions.throwAsIfUnchecked;

import java.io.IOException;

import org.junit.Test;


public class ExceptionsThrowTest {

    private void throwChecked() {
        try {
            throw new IOException();
        } catch (Exception e) {
            throwAsIfUnchecked(e);
        }
    }
    
    private void throwUnchecked() {
        try {
            throw new NullPointerException();
        } catch (Exception e) {
            throwAsIfUnchecked(e);
        }
    }
    
    @Test
    public void checkedExceptionBubblesUpAsIs() {
        try {
            throwChecked();
        } catch (Exception e) {
            assertTrue(e instanceof IOException);
            
            StackTraceElement throwSite = e.getStackTrace()[0];
            assertThat(throwSite.getMethodName(), is("throwChecked"));
        }
    }
    
    @Test
    public void uncheckedExceptionBubblesUpAsIs() {
        try {
            throwUnchecked();
        } catch (NullPointerException e) {
            StackTraceElement throwSite = e.getStackTrace()[0];
            assertThat(throwSite.getMethodName(), is("throwUnchecked"));
        }
    }
    
}

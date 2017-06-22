package util.error;

import static util.error.Exceptions.unchecked;

import java.io.IOException;
import java.util.function.Function;

import org.junit.Test;

import util.lambda.FunctionE;

public class ExceptionsFunctionTest {
    
    private String consumeWithCheckedException(int k) throws IOException {
        throw new IOException();
    }
    
    private String consumeWithUncheckedException(int k) {
        throw new NullPointerException();
    }
    
    private String feed(int k, Function<Integer, String> f) {
        return f.apply(k);
    }
    
    @Test(expected = IOException.class)
    public void checkedExceptionBubblesUpAsIs() {
        FunctionE<Integer, String> fe = this::consumeWithCheckedException;
        feed(1, fe);
    }
    
    @Test(expected = IOException.class)
    public void checkedExceptionBubblesUpAsIsUsingUnchecked() {
        feed(1, unchecked(this::consumeWithCheckedException));
    }
    
    @Test(expected = NullPointerException.class)
    public void uncheckedExceptionBubblesUpAsIs() {
        FunctionE<Integer, String> fe = this::consumeWithUncheckedException;
        feed(1, fe);
    }
    
    @Test(expected = NullPointerException.class)
    public void uncheckedExceptionBubblesUpAsIsUsingUnchecked() {
        feed(1, unchecked(this::consumeWithUncheckedException));
    }

}

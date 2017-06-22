package util.error;

import static util.error.Exceptions.unchecked;

import java.io.IOException;
import java.util.function.Consumer;

import org.junit.Test;

import util.lambda.ConsumerE;

public class ExceptionsConsumerTest {

    private void consumeWithCheckedException(int k) throws IOException {
        throw new IOException();
    }
    
    private void consumeWithUncheckedException(int k) {
        throw new NullPointerException();
    }
    
    private void feed(int k, Consumer<Integer> f) {
        f.accept(k);
    }
    
    @Test(expected = IOException.class)
    public void checkedExceptionBubblesUpAsIs() {
        ConsumerE<Integer> fe = this::consumeWithCheckedException;
        feed(1, fe);
    }
    
    @Test(expected = IOException.class)
    public void checkedExceptionBubblesUpAsIsUsingUnchecked() {
        feed(1, unchecked(this::consumeWithCheckedException));
    }
    
    @Test(expected = NullPointerException.class)
    public void uncheckedExceptionBubblesUpAsIs() {
        ConsumerE<Integer> fe = this::consumeWithUncheckedException;
        feed(1, fe);
    }
    
    @Test(expected = NullPointerException.class)
    public void uncheckedExceptionBubblesUpAsIsUsingUnchecked() {
        feed(1, unchecked(this::consumeWithUncheckedException));
    }
    
}

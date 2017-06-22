package util.error;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import static util.error.Exceptions.runAndCatch;
import static util.error.Exceptions.runAndSwallow;
import static util.error.Exceptions.runUnchecked;
import static util.error.Exceptions.unchecked;

import java.io.IOException;
import java.util.Optional;

import org.junit.Test;
import util.lambda.ActionE;


public class ExceptionsActionTest {

    private void actionWithException() throws IOException {
        throw new IOException();
    }

    private void throwWith(String msg) {
        throw new IllegalArgumentException(msg);
    }

    @Test(expected = NullPointerException.class)
    public void npeIfNullAction() {
        runUnchecked(null);
    }
    
    @Test(expected = IOException.class)
    public void exceptionBubblesUpAsIs() {
        runUnchecked(this::actionWithException);
    }

    @Test (expected = NullPointerException.class)
    public void uncheckedFailsFast() {
        unchecked((ActionE) null);
    }

    @Test(expected = IOException.class)
    public void exceptionBubblesUpAsIsFromRunnable() {
        Runnable adapter = unchecked(this::actionWithException);
        adapter.run();
    }

    @Test (expected = NullPointerException.class)
    public void runAndCatchThrowsIfNullArray() {
        runAndCatch((ActionE[]) null);
    }

    @Test (expected = NullPointerException.class)
    public void runAndCatchThrowsIfNullArrayElements() {
        runAndCatch(() -> {}, null);
    }

    @Test
    public void runAndCatchReturnsExceptionsInInputOrder() {
        Optional<Throwable>[] collected =
            runAndCatch(() -> {}, () -> throwWith("1"), () -> {},
                        () -> throwWith("3"), this::actionWithException);

        assertThat(collected.length, is(5));

        assertThat(collected[0], is(Optional.empty()));

        assertTrue(collected[1].isPresent());
        assertThat(collected[1].get().getMessage(), is("1"));

        assertThat(collected[2], is(Optional.empty()));

        assertTrue(collected[3].isPresent());
        assertThat(collected[3].get().getMessage(), is("3"));
        assertTrue(collected[3].get() instanceof IllegalArgumentException);

        assertTrue(collected[4].isPresent());
        assertTrue(collected[4].get() instanceof IOException);
    }

    @Test (expected = NullPointerException.class)
    public void runAndSwallowThrowsIfNullArray() {
        runAndSwallow((ActionE[]) null);
    }

    @Test (expected = NullPointerException.class)
    public void runAndSwallowThrowsIfNullArrayElements() {
        runAndSwallow(null, () -> {});
    }

    @Test
    public void runAndSwallowNeverThrows() {
        try {
            runAndSwallow(() -> { throw new Error(); });
        } catch (Throwable t) {
            fail("should have caught the error!");
        }
    }

}

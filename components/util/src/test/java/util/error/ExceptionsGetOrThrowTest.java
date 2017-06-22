package util.error;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.error.Exceptions.getOrThrow;
import static util.object.Either.left;
import static util.object.Either.right;

import java.io.IOException;

import org.junit.Test;

public class ExceptionsGetOrThrowTest {

    @Test(expected = NullPointerException.class)
    public void failIfNullInput() {
        getOrThrow(null);
    }
    
    @Test(expected = IOException.class)
    public void throwIfItsLeft() {
        getOrThrow(left(new IOException()));
    }
    
    @Test
    public void getIfItsRight() {
        String expected = "x";
        String actual = getOrThrow(right("x"));
        
        assertThat(actual, is(expected));
    }
}

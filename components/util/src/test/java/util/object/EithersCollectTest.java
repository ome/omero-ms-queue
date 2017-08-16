package util.object;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.object.Either.*;
import static util.object.Eithers.collectLeft;
import static util.object.Eithers.collectRight;
import static util.sequence.Arrayz.array;

import java.util.stream.Stream;

import org.junit.Test;

public class EithersCollectTest {
    
    @Test (expected = NullPointerException.class)
    public void collectLeftThrowsIfNullArg() {
        collectLeft(null);
    }
    
    @Test (expected = NullPointerException.class)
    public void collectRightThrowsIfNullArg() {
        collectRight(null);
    }
    
    @Test
    public void collectLeftReturnsEmptyOnEmptyInput() {
        Stream<Object> actual = collectLeft(Stream.empty());
        assertThat(actual.count(), is(0L));
    }
    
    @Test
    public void collectRightReturnsEmptyOnEmptyInput() {
        Stream<Object> actual = collectRight(Stream.empty());
        assertThat(actual.count(), is(0L));
    }
    
    @Test
    public void collectLeftFiltersNullsOut() {
        Stream<Integer> actual = collectLeft(Stream.of(right(1), null, right(3)));
        assertThat(actual.count(), is(0L));
    }
    
    @Test
    public void collectRightFiltersNullsOut() {
        Stream<Integer> actual = collectRight(Stream.of(left(1), null, left(3)));
        assertThat(actual.count(), is(0L));
    }
    
    @Test
    public void collectOneLeft() {
        Integer[] actual = collectLeft(Stream.of(right(1), null, left(3)))
                          .toArray(Integer[]::new);
        assertArrayEquals(array(3), actual);
    }
    
    @Test
    public void collectOneRight() {
        Integer[] actual = collectRight(Stream.of(right(1), null, left(3)))
                          .toArray(Integer[]::new);
        assertArrayEquals(array(1), actual);
    }
    
    @Test
    public void collectManyLeft() {
        Integer[] actual = collectLeft(
                            Stream.of(right(1), null, left(3), right(4), left(5)))
                          .toArray(Integer[]::new);
        assertArrayEquals(array(3, 5), actual);
    }
    
    @Test
    public void collectManyRight() {
        Integer[] actual = collectRight(
                            Stream.of(right(1), null, left(3), right(4), left(5)))
                          .toArray(Integer[]::new);
        assertArrayEquals(array(1, 4), actual);
    }

    @Test
    public void ctor() {
        new Eithers();  // only to get 100% coverage.
    }

}

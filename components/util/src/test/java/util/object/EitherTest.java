package util.object;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.object.Either.*;

import org.junit.Test;


public class EitherTest {

    @Test
    public void newLeftBuildsLeftValue() {
        Either<String, Integer> left = left("x");
        assertThat(left, notNullValue());
        assertThat(left.isLeft(), is(true));
        assertThat(left.isRight(), is(false));
        assertThat(left.getLeft(), equalTo("x"));
        assertThat(left.getRight(), nullValue());
    }
    
    @Test
    public void newRightBuildsRightValue() {
        Either<String, Integer> right = right(1);
        assertThat(right, notNullValue());
        assertThat(right.isLeft(), is(false));
        assertThat(right.isRight(), is(true));
        assertThat(right.getLeft(), nullValue());
        assertThat(right.getRight(), equalTo(1));
    }
    
    @Test(expected = NullPointerException.class)
    public void mapThrowsIfRightAndNullArg() {
        right(0).map(null);
    }

    @Test
    public void mapDoesntThrowIfLeftAndNullArg() {
        left("").map(null);
    }
    
    @Test
    public void mapPropagatesLeft() {
        Either<String, Integer> left = left("x");
        Either<String, Integer> mapped = left.map(x -> x + 1);
        
        assertTrue(left != mapped);
        assertThat(mapped.isLeft(), is(true));
        assertThat(mapped.getLeft(), equalTo(left.getLeft()));
    }
    
    @Test
    public void mapTransformsRight() {
        Either<String, Integer> right = right(1);
        Either<String, Integer> mapped = right.map(x -> x + 1);
        
        assertTrue(right != mapped);
        assertThat(mapped.isLeft(), is(false));
        assertThat(mapped.getRight(), equalTo(2));
    }
    
    @Test(expected = NullPointerException.class)
    public void bindThrowsIfRightAndNullArg() {
        right(0).bind(null);
    }

    @Test
    public void bindDoesntThrowIfLeftAndNullArg() {
        left("").bind(null);
    }
    
    @Test
    public void bindPropagatesLeft() {
        Either<String, Integer> left = left("x");
        Either<String, Integer> bound = left.bind(x -> right(x + 1));
        
        assertTrue(left != bound);
        assertThat(bound.isLeft(), is(true));
        assertThat(bound.getLeft(), equalTo(left.getLeft()));
    }
    
    @Test
    public void bindTransformsRight() {
        Either<String, Integer> right = right(1);
        Either<String, Integer> bound = right.bind(x -> right(x + 1));
        
        assertTrue(right != bound);
        assertThat(bound.isLeft(), is(false));
        assertThat(bound.getRight(), equalTo(2));
    }
    
    @Test(expected = NullPointerException.class)
    public void eitherThrowsIfFstArgNull() {
        right(0).either(null, x -> x + 1);
    }
    
    @Test(expected = NullPointerException.class)
    public void eitherThrowsIfSndArgNull() {
        right(0).either(x -> x, null);
    }
    
    @Test
    public void eitherMapsLeftWhenLeftValue() {
        Either<String, Integer> left = left("x");
        String xy = left.either(x -> x + "y", Object::toString);
        
        assertThat(xy, equalTo("xy"));
    }
    
    @Test
    public void eitherMapsRightWhenRightValue() {
        Either<String, Integer> right = right(1);
        Integer r = right.either(Integer::parseInt, x -> x + 1);
        
        assertThat(r, equalTo(2));
    }
    
}

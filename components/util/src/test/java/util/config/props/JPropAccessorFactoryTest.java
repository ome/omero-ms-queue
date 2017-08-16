package util.config.props;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import java.net.URI;
import java.util.Optional;
import java.util.Properties;


public class JPropAccessorFactoryTest {

    enum TestE { A }

    private static <T> void setGetAndAssert(
            JPropAccessor<T> target, T expected) {
        Properties db = new Properties();
        target.set(db, expected);
        Optional<T> actual = target.get(db);

        assertNotNull(actual);
        assertTrue(actual.isPresent());
        assertThat(actual.get(), is(expected));
    }

    @Test
    public void setAndGetString() {
        setGetAndAssert(
                JPropAccessorFactory.makeString(new JPropKey("k")), "v");
    }

    @Test
    public void setAndGetBool() {
        setGetAndAssert(
                JPropAccessorFactory.makeBool(new JPropKey("k")), true);
    }

    @Test
    public void setAndGetInt() {
        setGetAndAssert(
                JPropAccessorFactory.makeInt(new JPropKey("k")), 2);
    }

    @Test
    public void setAndGetURI() {
        setGetAndAssert(
                JPropAccessorFactory.makeURI(new JPropKey("k")),
                URI.create("http://some.host"));
    }

    @Test
    public void setAndGetEnum() {
        setGetAndAssert(
                JPropAccessorFactory.makeEnum(TestE.class, new JPropKey("k")),
                TestE.A);
    }

    @Test (expected = NullPointerException.class)
    public void makeThrowsIfNullKey() {
        JPropAccessorFactory.make(null, x -> x);
    }

    @Test (expected = NullPointerException.class)
    public void makeThrowsIfNullConverter() {
        JPropAccessorFactory.make(new JPropKey("k"), null);
    }

    @Test (expected = NullPointerException.class)
    public void makeStringThrowsIfNullKey() {
        JPropAccessorFactory.makeString(null);
    }

    @Test (expected = NullPointerException.class)
    public void makeBoolThrowsIfNullKey() {
        JPropAccessorFactory.makeBool(null);
    }

    @Test (expected = NullPointerException.class)
    public void makeIntThrowsIfNullKey() {
        JPropAccessorFactory.makeInt(null);
    }

    @Test (expected = NullPointerException.class)
    public void makeURIThrowsIfNullKey() {
        JPropAccessorFactory.makeURI(null);
    }

    @Test (expected = NullPointerException.class)
    public void makeEnumThrowsIfEnumTypeKey() {
        JPropAccessorFactory.makeEnum(null, new JPropKey("k"));
    }

    @Test (expected = NullPointerException.class)
    public void makeEnumThrowsIfNullKey() {
        JPropAccessorFactory.makeEnum(TestE.class, null);
    }

    @Test
    public void ctor() {
        new JPropAccessorFactory();  // only to get 100% coverage.
    }

}

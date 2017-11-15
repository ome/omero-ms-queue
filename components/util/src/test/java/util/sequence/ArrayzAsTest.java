package util.sequence;


import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ArrayzAsTest {

    @Test
    public void asList() {
        assertThat(Arrayz.asList((String[])null).size(), is(0));
        assertThat(Arrayz.asList().size(), is(0));

        List<String> xs = Arrayz.asList("");
        assertThat(xs.size(), is(1));
        assertThat(xs.get(0), is(""));
    }

    @Test
    public void asMutableList() {
        assertThat(Arrayz.asMutableList((String[])null).size(), is(0));
        assertThat(Arrayz.asMutableList().size(), is(0));

        List<String> xs = Arrayz.asMutableList("");
        assertThat(xs.size(), is(1));
        assertThat(xs.get(0), is(""));

        xs.add("xxx");
        assertThat(xs.size(), is(2));
        assertThat(xs.get(0), is(""));
        assertThat(xs.get(1), is("xxx"));
    }

    @Test
    public void asStream() {
        assertThat(Arrayz.asStream((String[])null).count(), is(0L));
        assertThat(Arrayz.asStream().count(), is(0L));

        assertThat(Arrayz.asStream("").count(), is(1L));
        assertThat(Arrayz.asStream("").findFirst().get(), is(""));
    }

}

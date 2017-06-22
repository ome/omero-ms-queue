package util.sequence;

import java.util.stream.Stream;

import util.alg.MonoidProvider;

/**
 * A monoid whose values are streams carrying {@code T}-values; the product is
 * {@link Stream#concat(Stream, Stream) concatenation} and the unit is the
 * {@link Stream#empty() empty} stream.
 * Conceptually, this is the <a href="https://en.wikipedia.org/wiki/Free_monoid">
 * free monoid</a> on {@code T}-values.
 */
public class StreamMonoid<T> extends MonoidProvider<Stream<T>> {

    public StreamMonoid() {
        super(Stream::concat, Stream::empty);
    }
    
}

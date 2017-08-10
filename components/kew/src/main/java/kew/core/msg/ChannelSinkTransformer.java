package kew.core.msg;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

/**
 * Makes a {@link ChannelSink} work with a different data type.
 * Specifically, given a target sink that consumes {@code T}-values and a data
 * transformation function {@code f : S → T}, we build a sink that consumes
 * {@code S}-values simply by taking each input {@code S}-value {@code s} and
 * making the target sink consume {@code f(s)}.
 * @param <S> The type of values consumed by the target sink.
 * @param <T> The type of values consumed by the transformed sink.
 */
public class ChannelSinkTransformer<S, T> implements ChannelSink<S> {

    private final Function<S, T> transformer;
    private final ChannelSink<T> target;

    /**
     * Creates a new instance.
     * @param transformer the data transformation function.
     * @param target the target sink that will consume the data.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public ChannelSinkTransformer(Function<S, T> transformer,
                                  ChannelSink<T> target) {
        requireNonNull(transformer, "transformer");
        requireNonNull(target, "target");

        this.transformer = transformer;
        this.target = target;
    }

    @Override
    public void consume(S data) {
        requireNonNull(data, "data");

        T targetData = transformer.apply(data);
        target.consume(targetData);
    }

}
/* NOTE. Functoriality.
 * You could say this is a poor man's functor even if technically I'm not sure
 * what the hell it actually is. But Java being Java, I'm not gonna bother
 * finding out. Though if you squint your eyes it seems we might have some
 * sort of contravariant puppy in our hands:
 *
 *         S                   S → ()     (this should probably be some
 *              ChannelSink                kind of Kleisli category)
 *       f ↓  -------------->    ↑
 *
 *         T                   T → ()
 */
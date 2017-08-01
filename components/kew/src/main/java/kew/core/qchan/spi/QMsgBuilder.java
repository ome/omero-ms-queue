package kew.core.qchan.spi;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Convenience alias for a function that creates a queue message given a
 * {@link QMsgFactory message factory}.
 * @param <QM> the message type in the underlying middleware.
 */
@FunctionalInterface
public interface QMsgBuilder<QM> extends Function<QMsgFactory<QM>, QM> {

    /**
     * Convenience function composition alias to use for fluent setting of
     * message properties.
     * @param transformer transforms the message produced by this builder.
     * @return the composite of the transformer after this builder.
     * @throws NullPointerException if the argument is {@code null}.
     */
    default QMsgBuilder<QM> with(Function<QM, QM> transformer) {
        requireNonNull(transformer, "transformer");
        return factory -> transformer.apply(apply(factory));
    }

    /**
     * Convenience function composition alias to use for fluent setting of
     * message properties.
     * @param setter sets some value in the message produced by this builder.
     * @return a function that first applies this builder, then the setter,
     * and then returns the message.
     * @throws NullPointerException if the argument is {@code null}.
     */
    default QMsgBuilder<QM> with(Consumer<QM> setter) {
        requireNonNull(setter, "setter");
        return factory -> {
            QM msg = apply(factory);
            setter.accept(msg);
            return msg;
        };
    }

}

package kew.providers.q.spi;

import java.util.function.Function;

/**
 * Convenience alias for a function that creates a queue message given a
 * {@link QMsgFactory message factory}.
 * @param <QM> the message type in the underlying middleware.
 */
public interface QMsgBuilder<QM> extends Function<QMsgFactory, QM> {
}

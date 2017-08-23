package kew.core.qchan.spi;

import util.object.Builder;

/**
 * Convenience alias for a function that creates a queue message given a
 * {@link QMsgFactory message factory}. Use for fluent setting of message
 * properties.
 * @param <QM> the message type in the underlying middleware.
 */
@FunctionalInterface
public interface QMsgBuilder<QM> extends Builder<QMsgFactory<QM>, QM> {

}

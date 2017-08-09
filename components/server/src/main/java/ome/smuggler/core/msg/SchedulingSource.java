package ome.smuggler.core.msg;

import util.types.FutureTimepoint;

/**
 * A channel source that allows the scheduling of messages to send. 
 * The message metadata specifies a future time-point at which the message 
 * should be delivered.
 */
public interface SchedulingSource<T> extends MessageSource<FutureTimepoint, T> {

}

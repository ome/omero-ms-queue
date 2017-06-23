package ome.smuggler.core.msg;

/**
 * Indicates whether a message should be re-delivered after being consumed.
 * @see MessageRepeater
 */
public enum RepeatAction {

    /**
     * A message consumer uses this flag to cause re-delivery of the message.
     */
    Repeat, 
    
    /**
     * A message consumer uses this flag to indicate the message has been
     * consumed fully and must not be re-delivered.
     */
    Stop
    
}

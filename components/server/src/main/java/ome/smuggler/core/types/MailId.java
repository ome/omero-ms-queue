package ome.smuggler.core.types;

import util.types.UuidString;

/**
 * Identifies a mail delivery.
 */
public class MailId extends UuidString {
    
    public MailId(String uuid) {
        super(uuid);
    }
    
    public MailId() {
        super();
    }
    
}
package ome.smuggler.core.types;

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
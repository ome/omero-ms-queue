package ome.smuggler.core.types;

import static java.util.Objects.requireNonNull;
import static util.string.Strings.requireString;

import java.util.Objects;

/**
 * A plain text email message.
 */
public class PlainTextMail {

    private final Email recipient;
    private final String subject;
    private final String content;
    
    /**
     * Creates a new instance.
     * @param recipient the recipient's email address.
     * @param subject the email's subject line.
     * @param content the email's message body.
     * @throws NullPointerException if the {@code recipient} is {@code null}.
     * @throws IllegalArgumentException if any of the string arguments is
     * {@code null} or empty.
     */
    public PlainTextMail(Email recipient, String subject, String content) {
        requireNonNull(recipient, "recipient");
        requireString(subject, "subject");
        requireString(content, "content");
        
        this.recipient = recipient;
        this.subject = subject;
        this.content = content;
    }

    /**
     * @return the recipient's email address.
     */
    public Email getRecipient() {
        return recipient;
    }

    /**
     * @return the email's subject line.
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @return the email's message body.
     */
    public String getContent() {
        return content;
    }
    
    @Override
    public boolean equals(Object x) {
        if (this == x) {
            return true;
        }
        if (x instanceof PlainTextMail) {
            PlainTextMail other = (PlainTextMail) x;
            return Objects.equals(recipient, other.recipient)
                && Objects.equals(subject, other.subject)
                && Objects.equals(content, other.content);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(recipient, subject, content);
    }
    
}

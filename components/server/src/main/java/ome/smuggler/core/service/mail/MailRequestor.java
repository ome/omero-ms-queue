package ome.smuggler.core.service.mail;

import ome.smuggler.core.types.MailId;
import ome.smuggler.core.types.PlainTextMail;

/**
 * Requests the sending of an email.
 */
public interface MailRequestor {
    
    /**
     * Adds the email request to the queue and returns immediately. 
     * The request will subsequently be fetched from the mail queue and 
     * serviced as needed.
     * @param request details what to email message to send.
     * @return a token to use to get hold of this mail request.
     * @throws NullPointerException if the argument is {@code null}.
     */
    MailId enqueue(PlainTextMail request);

}

package ome.smuggler.core.service.mail;

import java.io.OutputStream;

import ome.smuggler.core.types.PlainTextMail;

/**
 * Defines the functionality our services need to send mail.
 * An external service provider supplies the actual implementation.
 */
public interface MailClient {
    
    /**
     * Relays a mail message to a server.
     * @param data the message to send.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws MailClientException if a sending error occurs.
     */
    void send(PlainTextMail data);

    /**
     * Writes a mail message to the given stream.
     * @param data the message to send.
     * @param destination where to output the message.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws MailClientException if an I/O or MIME message error occurs.
     */
    void stream(PlainTextMail data, OutputStream destination);

}

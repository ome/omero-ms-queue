package ome.smuggler.core.service.mail;

public class MailClientException extends RuntimeException {

    private static final long serialVersionUID = -9139040000089145986L;

    public MailClientException(Exception cause) {
        super(cause);
    }
    
}

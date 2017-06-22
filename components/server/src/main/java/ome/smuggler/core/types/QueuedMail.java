package ome.smuggler.core.types;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

/**
 * Represents an email message that has been queued and is waiting to be sent.
 */
public class QueuedMail {

    private final MailId taskId;
    private final PlainTextMail request;
    

    /**
     * Creates a new instance.
     * @param taskId the ID assigned to the mail sending task.
     * @param request the email data to send.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public QueuedMail(MailId taskId, PlainTextMail request) {
        requireNonNull(taskId, "taskId");
        requireNonNull(request, "request");
        
        this.taskId = taskId;
        this.request = request;
    }

    /**
     * @return the ID assigned to the import task.
     */
    public MailId getTaskId() {
        return taskId;
    }

    /**
     * @return the data detailing what the import task must do.
     */
    public PlainTextMail getRequest() {
        return request;
    }
    
    @Override
    public boolean equals(Object x) {
        if (this == x) {
            return true;
        }
        if (x instanceof QueuedMail) {
            QueuedMail other = (QueuedMail) x;
            return Objects.equals(taskId, other.taskId)
                && Objects.equals(request, other.request);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(taskId, request);
    }

}

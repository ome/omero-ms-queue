package ome.smuggler.core.service.mail.impl;

import static java.util.Objects.requireNonNull;

import ome.smuggler.core.service.mail.FailedMailHandler;
import ome.smuggler.core.types.QueuedMail;

/**
 * Implements {@link FailedMailHandler} to store failed mail messages in a
 * local directory.
 */
public class MailFailureHandler implements FailedMailHandler {

    private final MailEnv env;
    
    public MailFailureHandler(MailEnv env) {
        requireNonNull(env, "env");
        this.env = env;
    }
    
    @Override
    public void accept(QueuedMail data) {
        env.log().failedMail(data);
        
        env.failedMailStore().add(data.getTaskId(), 
                outputStream -> env.mailClient()
                                   .stream(data.getRequest(), outputStream));
    }

}

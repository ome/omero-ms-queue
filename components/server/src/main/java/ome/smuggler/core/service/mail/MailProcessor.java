package ome.smuggler.core.service.mail;

import kew.core.msg.RepeatAction;
import kew.core.msg.RepeatConsumer;
import ome.smuggler.core.types.QueuedMail;

/**
 * Carries out a request to send an email.
 * It consumes a mail request that was fetched from the mail queue and returns 
 * {@link RepeatAction#Repeat Repeat} if the sending of the email failed because
 * of a transient error condition and it should be retried or {@link 
 * RepeatAction#Stop Stop} if the sending should not be retried because it 
 * succeeded or it failed but it's not possible to recover.
 */
public interface MailProcessor extends RepeatConsumer<QueuedMail> {

}

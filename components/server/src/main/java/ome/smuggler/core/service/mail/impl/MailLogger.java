package ome.smuggler.core.service.mail.impl;

import static util.object.Pair.pair;

import java.io.PrintWriter;
import java.util.function.Consumer;
import java.util.stream.Stream;

import ome.smuggler.core.service.log.BaseLogger;
import ome.smuggler.core.service.log.LogService;
import ome.smuggler.core.types.PlainTextMail;
import ome.smuggler.core.types.QueuedMail;
import util.object.Pair;

/**
 * Logging methods for the mail service.
 */
public class MailLogger extends BaseLogger {

    private static Stream<Pair<Object, Object>> summary(QueuedMail data) {
        PlainTextMail req = data.getRequest();
        return Stream.of(
                pair("To", req.getRecipient().get()),
                pair("Subject", req.getSubject()));
    }
    
    private static Consumer<PrintWriter> writer(String header, 
            QueuedMail data) {
        return fieldsWriter(header, summary(data));
    }
    
    public MailLogger(LogService service) {
        super(service);
    }

    public void mailSent(QueuedMail data) {
        info(data, writer("Email sent.", data));
    }
    
    public void failedMail(QueuedMail data) {
        error(data, writer("Failed to send email.", data));
    }
    
}

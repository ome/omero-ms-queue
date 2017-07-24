package ome.smuggler.core.service.imports.impl;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static util.sequence.Arrayz.array;
import static util.sequence.Streams.concat;

import java.util.Set;
import java.util.stream.Stream;

import ome.smuggler.core.types.ImportBatchStatus;
import ome.smuggler.core.types.PlainTextMail;
import ome.smuggler.core.types.QueuedImport;
import util.network.Hostname;
import util.types.Email;

/**
 * Builds email messages to notify users and sys admins of the processing
 * outcome of an import batch.
 */
public class ImportMailFormatter {

    public static final String CRLF = "\r\n";

    private static final String SuccessSubject = "OMERO import succeeded";
    private static final String SuccessMessage =
        "Your image data on %s was successfully imported into OMERO.";

    private static String successSubject() {
        return SuccessSubject;
    }

    private static String successMessage() {
        return String.format(SuccessMessage, Hostname.lookup());
    }

    private static final String FailureSubject = "Failed OMERO import [ref. %s]";
    private static final String FailureMessagePart1 =
            "Some of your image data on %s failed to import into OMERO. " +
            "Please contact your imaging facility staff.";
    private static final String[] FailureMessagePart2 = array(
            "",
            "FAILED. The following files have failed to import.",
            "--------------------------------------------------"
    );
    private static final String[] FailureMessagePart3 = array(
            "",
            "SUCCEEDED. The following files have been imported.",
            "--------------------------------------------------"
    );

    private static String failureSubject(String refNo) {
        return String.format(FailureSubject, refNo);
    }

    private static String failureMessage(Stream<String> failedFiles,
                                         Stream<String> succeededFiles) {
        String part1 = String.format(FailureMessagePart1, Hostname.lookup());
        return concat(Stream.of(part1),
                      Stream.of(FailureMessagePart2),
                      failedFiles,
                      Stream.of(FailureMessagePart3),
                      succeededFiles)
              .collect(joining(CRLF));
    }

    private final ImportBatchStatus outcome;

    /**
     * Creates a new instance.
     * @param outcome the state of the import batch after all its imports have
     *                been processed.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public ImportMailFormatter(ImportBatchStatus outcome) {
        requireNonNull(outcome, "status");
        this.outcome = outcome;
    }

    private Stream<String> list(Set<QueuedImport> qs) {
        return qs.stream()
                 .map(qi -> qi.getRequest().getTarget())
                 .map(Object::toString);
    }

    private PlainTextMail buildFailureMail(Email recipient) {
        String subject = failureSubject(outcome.batch().batchId().id());
        String content = failureMessage(list(outcome.failed()),
                                        list(outcome.succeeded()));

        return new PlainTextMail(recipient, subject, content);
    }

    private PlainTextMail buildSuccessMail(Email recipient) {
        return new PlainTextMail(recipient,
                                 successSubject(),
                                 successMessage());
    }

    /**
     * Builds an email messages to notify the specified recipient of the
     * processing outcome of the import batch.
     * @param success whether the batch succeeded or failed.
     * @param recipient who to send the email to.
     * @return the email message.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public PlainTextMail buildMailMessage(boolean success, Email recipient) {
        requireNonNull(recipient, "recipient");

        return success ? buildSuccessMail(recipient)
                       : buildFailureMail(recipient);
    }

}

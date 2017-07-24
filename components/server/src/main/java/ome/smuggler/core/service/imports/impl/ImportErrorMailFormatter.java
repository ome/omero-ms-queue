package ome.smuggler.core.service.imports.impl;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static ome.smuggler.core.service.imports.impl.ImportMailFormatter.CRLF;
import static util.string.Strings.write;

import java.util.Optional;
import java.util.stream.Stream;

import ome.smuggler.core.types.PlainTextMail;
import ome.smuggler.core.types.QueuedImport;
import util.network.Hostname;
import util.types.Email;

/**
 * Formats email messages to send out to sys admins when an import within a
 * batch fails.
 */
public class ImportErrorMailFormatter {

    private static final String Subject = "Failed OMERO import [ref. %s]";
    private static final String MessagePart1 =
            "Image data on %s failed to import into OMERO. " +
                    "The import will be retried if retries are configured.";
    private static final String MessagePart2 =
            "Additional information: ";


    private final QueuedImport importData;
    private final Optional<Exception> maybeE;

    /**
     * Creates a new instance.
     * @param importData the import that failed.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public ImportErrorMailFormatter(QueuedImport importData) {
        requireNonNull(importData, "importData");

        this.importData = importData;
        this.maybeE = Optional.empty();
    }

    /**
     * Creates a new instance.
     * @param importData the import that failed.
     * @param e an exception that was raised during the import process.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public ImportErrorMailFormatter(QueuedImport importData, Exception e) {
        requireNonNull(importData, "importData");
        requireNonNull(e, "e");

        this.importData = importData;
        this.maybeE = Optional.of(e);
    }

    private String subject() {
        return String.format(Subject, importData.getTaskId().id());
    }

    private String content() {
        String part1 = String.format(MessagePart1, Hostname.lookup());
        if (maybeE.isPresent()) {
            String stackTrace = write(p -> maybeE.get().printStackTrace(p));
            return Stream.of(part1, MessagePart2, stackTrace)
                         .collect(joining(CRLF));
        } else {
            return part1;
        }
    }

    /**
     * Builds an email messages to notify the specified recipient that the
     * import failed but will be retried.
     * @param recipient who to send the email to.
     * @return the email message.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public PlainTextMail buildMailMessage(Email recipient) {
        requireNonNull(recipient, "recipient");

        return new PlainTextMail(recipient, subject(), content());
    }

}

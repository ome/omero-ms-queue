package ome.smuggler.core.service.imports.impl;

import static java.util.Objects.requireNonNull;

import ome.smuggler.core.types.PlainTextMail;
import ome.smuggler.core.types.QueuedImport;


/**
 * Triggers the sending of an import error notification email.
 * Even though the import may be retried, we send out an alert to the sys admin
 * to give them a chance to fix the issue before the import is retried. This
 * alert is independent of the {@link ImportOutcomeNotifier batch outcome
 * notification}, it's just an extra alert we send out to sys admins.
 */
public class ImportErrorNotifier {

    private final ImportEnv env;

    /**
     * Creates a new instance.
     * @param env the import environment.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public ImportErrorNotifier(ImportEnv env) {
        requireNonNull(env, "env");
        this.env = env;
    }

    private void maybeNotify(ImportErrorMailFormatter formatter) {
        if (env.sysAdminEmail().isPresent()) {
            PlainTextMail message = formatter.buildMailMessage(
                                                env.sysAdminEmail().get());
            env.mail().enqueue(message);
        }
    }

    /**
     * Sends an alert email to the sys admin if a sys admin email is configured.
     * Does nothing otherwise.
     * @param importData the import that failed.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public void notifyFailure(QueuedImport importData) {
        maybeNotify(new ImportErrorMailFormatter(importData));
    }

    /**
     * Sends an alert email to the sys admin if a sys admin email is configured.
     * Does nothing otherwise.
     * @param importData the import that failed.
     * @param e unforeseen error occurred while running the import.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public void notifyFailure(QueuedImport importData, Exception e) {
        maybeNotify(new ImportErrorMailFormatter(importData, e));
    }

}

package ome.smuggler.core.service.imports.impl;

import static java.util.Objects.requireNonNull;

import ome.smuggler.core.types.Email;
import ome.smuggler.core.types.ImportBatchStatus;
import ome.smuggler.core.types.PlainTextMail;

/**
 * Triggers the sending of import outcome notification emails.
 * One email will go out to the user who requested the import. The email is just
 * a one-liner success notification message if the batch succeeded. Otherwise
 * the email contains a detailed list of the files that were imported
 * successfully and a list of those that failed. In the case of failure, this
 * same email is also sent to the system administrator, as long as one was
 * configured.
 */
public class ImportOutcomeNotifier {

    /**
     * Picks the email of an unspecified import request.
     * @param status the batch status.
     * @return the email.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static Email findAnyEmail(ImportBatchStatus status) {
        requireNonNull(status, "status");
        return status.batch()
                     .imports()
                     .findFirst()
                     .get()                    // (*)
                     .getRequest()
                     .getExperimenterEmail();
    }
    // (*) It's not possible to construct an ImportBatch with no imports---ctor
    // throws if no imports are passed in.

    private final ImportEnv env;
    private final ImportBatchStatus outcome;
    private final ImportMailFormatter formatter;

    /**
     * Creates a new instance.
     * @param env the import environment.
     * @param outcome the import batch state after all its imports have been
     *                processed.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public ImportOutcomeNotifier(ImportEnv env, ImportBatchStatus outcome) {
        requireNonNull(env, "env");
        requireNonNull(outcome, "outcome");
        
        this.env = env;
        this.outcome = outcome;
        this.formatter = new ImportMailFormatter(outcome);
    }

    private Email experimenterEmail() {
        return findAnyEmail(outcome);
    }
    /* NOTE. Assuming all the imports in the batch are for the same user.
     * We could easily drop this assumption and group imports by user and
     * then send a report email for each user. But I'll leave this for
     * Smuggler v2 as it's not needed for now.
     */

    private void notifyUserWhoRequestedImport() {
        PlainTextMail message = formatter.buildMailMessage(
                                                outcome.allSucceeded(),
                                                experimenterEmail());
        env.mail().enqueue(message);
    }

    private void notifySysAdminOnFailure() {
        if (!outcome.allSucceeded() && env.sysAdminEmail().isPresent()) {
            PlainTextMail message = formatter.buildMailMessage(
                                                    false,
                                                    env.sysAdminEmail().get());
            env.mail().enqueue(message);
        }
    }

    /**
     * Triggers the sending of import outcome notification emails.
     */
    public void notifyOutcome() {
        notifyUserWhoRequestedImport();
        notifySysAdminOnFailure();
    }

}

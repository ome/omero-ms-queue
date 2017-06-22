package ome.smuggler.core.service.imports.impl;

import ome.smuggler.core.types.Email;
import ome.smuggler.core.types.ImportBatchStatus;
import ome.smuggler.core.types.PlainTextMail;
import ome.smuggler.core.types.ProcessedImport;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;

import static ome.smuggler.core.service.imports.impl.Utils.*;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assume.assumeThat;
import static org.mockito.Mockito.*;
import static util.sequence.Arrayz.array;

@RunWith(Theories.class)
public class ImportOutcomeNotifierTest {

    @DataPoints
    public static String[] emails = array("", "sys@admin.edu");

    @DataPoints
    @SuppressWarnings("unchecked")
    public static ImportBatchStatus[] outcomes = array(
        createAndProcessBatch(ProcessedImport::succeeded),
        createAndProcessBatch(ProcessedImport::failed),
        createAndProcessBatch(ProcessedImport::failed, ProcessedImport::succeeded),
        createAndProcessBatch(ProcessedImport::succeeded, ProcessedImport::succeeded)
    );

    private static Matcher<PlainTextMail> hasRecipientOf(String expected) {
        return new ArgumentMatcher<PlainTextMail>() {
            @Override
            public boolean matches(Object item) {
                if (item instanceof PlainTextMail) {
                    PlainTextMail m = (PlainTextMail) item;
                    return m.getRecipient().get().equals(expected);
                }
                return false;
            }
        };
    }

    @Theory
    public void userIsAlwaysNotified(
            ImportBatchStatus outcome, String sysAdminEmail) {
        ImportEnv env = mockedImportEnvWithMemBatchStore(sysAdminEmail);
        ImportOutcomeNotifier target = new ImportOutcomeNotifier(env, outcome);
        target.notifyOutcome();

        Email user = ImportOutcomeNotifier.findAnyEmail(outcome);
        verify(env.mail(), times(1)).enqueue(
                argThat(hasRecipientOf(user.get())));
    }

    @Theory
    public void notifyUserOnlyIfNoSysAdminEmailConfigured(
            ImportBatchStatus outcome, String sysAdminEmail) {
        assumeThat(sysAdminEmail, isEmptyOrNullString());

        ImportEnv env = mockedImportEnvWithMemBatchStore(sysAdminEmail);
        ImportOutcomeNotifier target = new ImportOutcomeNotifier(env, outcome);
        target.notifyOutcome();

        verify(env.mail(), times(1)).enqueue(any());
    }

    @Theory
    public void sysAdminIsNotifiedOnlyOnFailure(
            ImportBatchStatus outcome, String sysAdminEmail) {
        assumeThat(sysAdminEmail, not(isEmptyOrNullString()));

        ImportEnv env = mockedImportEnvWithMemBatchStore(sysAdminEmail);
        ImportOutcomeNotifier target = new ImportOutcomeNotifier(env, outcome);
        target.notifyOutcome();

        int t = outcome.allSucceeded() ? 0 : 1;
        verify(env.mail(), times(t)).enqueue(
                argThat(hasRecipientOf(sysAdminEmail)));
    }

    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfNullEnv() {
        new ImportOutcomeNotifier(null,
                createAndProcessBatch(ProcessedImport::succeeded));
    }

    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfNullOutcome() {
        new ImportOutcomeNotifier(dummyImportEnv(), null);
    }

}

package ome.smuggler.core.service.imports.impl;

import static org.mockito.Mockito.*;
import static ome.smuggler.core.service.imports.impl.Utils.*;

import org.junit.Test;
import org.hamcrest.Matcher;
import org.mockito.ArgumentMatcher;

import java.util.function.Predicate;

import ome.smuggler.core.types.PlainTextMail;
import ome.smuggler.core.types.QueuedImport;
import util.network.Hostname;

public class ImportErrorNotifierTest {

    private static Predicate<PlainTextMail> hasRecipientOf(String expected) {
        return m -> m.getRecipient().get().equals(expected);
    }

    private static Predicate<PlainTextMail> subjectContainsImportId(
            QueuedImport expected) {
        return m -> m.getSubject().contains(expected.getTaskId().id());
    }

    private static Predicate<PlainTextMail> bodyContainsHostname() {
        String expected = Hostname.lookup();
        return m -> m.getContent().contains(expected);
    }

    private static Matcher<PlainTextMail> checkMessage(
            Predicate<PlainTextMail> p) {
        return new ArgumentMatcher<PlainTextMail>() {
            @Override
            public boolean matches(Object item) {
                if (item instanceof PlainTextMail) {
                    PlainTextMail m = (PlainTextMail) item;
                    return p.test(m);
                }
                return false;
            }
        };
    }

    @Test
    public void doNothingIfNoSysAdminEmailConfigured() {
        ImportEnv env = mockedImportEnvWithMemBatchStore("");
        ImportErrorNotifier target = new ImportErrorNotifier(env);

        target.notifyFailure(newQueuedImport());
        verify(env.mail(), times(0)).enqueue(any());
    }

    @Test
    public void sendAlertEmail() {
        String sysAdminEmail = "sys@bru.za";
        ImportEnv env = mockedImportEnvWithMemBatchStore(sysAdminEmail);
        ImportErrorNotifier target = new ImportErrorNotifier(env);
        QueuedImport failed = newQueuedImport();

        target.notifyFailure(failed);

        Matcher<PlainTextMail> matches = checkMessage(
                                         hasRecipientOf(sysAdminEmail)
                                         .and(subjectContainsImportId(failed))
                                         .and(bodyContainsHostname()));
        verify(env.mail(), times(1)).enqueue(argThat(matches));
    }

    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfNullEnv() {
        new ImportErrorNotifier(null);
    }

    @Test(expected = NullPointerException.class)
    public void notifyThrowsIfNullImport() {
        new ImportErrorNotifier(dummyImportEnv())
                .notifyFailure(null);
    }

    @Test(expected = NullPointerException.class)
    public void notify2ThrowsIfNullImport() {
        new ImportErrorNotifier(dummyImportEnv())
                .notifyFailure(null, new Exception());
    }

    @Test(expected = NullPointerException.class)
    public void notify2ThrowsIfNullException() {
        new ImportErrorNotifier(dummyImportEnv())
                .notifyFailure(newQueuedImport(), null);
    }

}

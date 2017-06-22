package ome.smuggler.web.omero;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.junit.Test;
import java.util.Optional;

import ome.smuggler.core.service.omero.SessionService;
import ome.smuggler.web.Error;
import util.object.Either;

public class CreateSessionSubmitterTest {

    private static final String sessionKey = "sesh";

    private static SessionService mockSuccess() {
        SessionService service = mock(SessionService.class);
        when(service.createAndKeepAlive(any(), any(), any(), any()))
        .thenReturn(Optional.of(sessionKey));

        return service;
    }

    private static SessionService mockFailure() {
        SessionService service = mock(SessionService.class);
        when(service.createAndKeepAlive(any(), any(), any(), any()))
                .thenReturn(Optional.empty());

        return service;
    }

    private static Either<Error, CreateSessionResponse> runSubmit(
            CreateSessionRequest in, Optional<String> out,
            boolean shouldCallService) {
        SessionService service = mock(SessionService.class);
        when(service.createAndKeepAlive(any(), any(), any(), any()))
        .thenReturn(out);

        CreateSessionSubmitter target = new CreateSessionSubmitter(service);
        Either<Error, CreateSessionResponse> result = target.submit(in);

        if (shouldCallService) {
            CreateSessionRequestValidator v = new CreateSessionRequestValidator();
            v.validate(in);

            verify(service, times(1))
                .createAndKeepAlive(v.getOmero(), v.getUsername(),
                                    v.getPassword(), v.getKeepAlive());
        } else {
            verify(service, times(0))
                .createAndKeepAlive(any(), any(), any(), any());
        }

        return result;
    }

    private static void assertError(
            CreateSessionRequest in, Optional<String> out,
            boolean shouldCallService) {
        Either<Error, CreateSessionResponse> outcome =
                runSubmit(in, out, shouldCallService);

        assertNotNull(outcome);
        assertTrue(outcome.isLeft());

        Error e = outcome.getLeft();
        assertNotNull(e);
        assertThat(e.reason, not(isEmptyOrNullString()));
    }

    private static void assertSuccess(
            CreateSessionRequest in, String sessionKey) {
        Either<Error, CreateSessionResponse> outcome = runSubmit(
                in, Optional.of(sessionKey), true);

        assertNotNull(outcome);
        assertTrue(outcome.isRight());

        CreateSessionResponse r = outcome.getRight();
        assertNotNull(r);
        assertThat(r.sessionKey, is(sessionKey));
    }

    @Test
    public void errorOnValidationFailure() {
        CreateSessionRequest badInput = Utils.minValidCreateSessionRequest();
        badInput.omeroHost = null;

        assertError(badInput, Optional.of("sessionKey"), false);
    }

    @Test
    public void errorOnServiceFailure() {
        assertError(Utils.minValidCreateSessionRequest(),
                    Optional.empty(),
                    true);
    }

    @Test
    public void successfulSumbission() {
        assertSuccess(Utils.minValidCreateSessionRequest(), "sessionKey");
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullService() {
        new CreateSessionSubmitter(null);
    }

}

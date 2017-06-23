package ome.smuggler.web.omero;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ome.smuggler.core.types.OmeroDefault;
import ome.smuggler.web.Error;
import util.object.Either;


public class CreateSessionRequestValidatorTest {

    private CreateSessionRequestValidator validator;
    private CreateSessionRequest minValidInput;

    private void assertFailure(CreateSessionRequest input) {
        Either<Error, CreateSessionRequest> outcome = validator.validate(input);
        assertNotNull(outcome);
        assertTrue(outcome.isLeft());
        assertNotNull(outcome.getLeft());
        assertThat(outcome.getLeft().reason, is(not("")));
    }

    private void assertSuccess(CreateSessionRequest input) {
        Either<Error, CreateSessionRequest> outcome = validator.validate(input);
        assertNotNull(outcome);
        assertTrue(outcome.isRight());
        assertThat(outcome.getRight(), is(input));
    }

    @Before
    public void setup() {
        validator = new CreateSessionRequestValidator();
        minValidInput = Utils.minValidCreateSessionRequest();
    }

    @Test
    public void failIfNullInput() {
        assertFailure(null);
    }

    @Test
    public void failIfMissingOmeroHost() {
        minValidInput.omeroHost = null;
        assertFailure(minValidInput);
    }

    @Test
    public void failIfInvalidOmeroPort() {
        minValidInput.omeroPort = "x";
        assertFailure(minValidInput);
    }

    @Test
    public void failIfMissingUsername() {
        minValidInput.username = null;
        assertFailure(minValidInput);
    }

    @Test
    public void failIfMissingPassword() {
        minValidInput.password = null;
        assertFailure(minValidInput);
    }

    @Test
    public void failIfInvalidKeepAliveDuration() {
        minValidInput.keepAliveDuration = "x";
        assertFailure(minValidInput);
    }

    @Test
    public void failIfKeepAliveDurationIsZero() {
        minValidInput.keepAliveDuration = "0";
        assertFailure(minValidInput);
    }

    @Test
    public void failIfKeepAliveDurationIsNegative() {
        minValidInput.keepAliveDuration = "-123";
        assertFailure(minValidInput);
    }

    @Test
    public void succeedIfAllMinRequiredFieldsSupplied() {
        assertSuccess(minValidInput);
    }

    @Test
    public void succeedIfAllRequiredFieldsSupplied() {
        minValidInput.omeroPort = "123";
        minValidInput.keepAliveDuration = "321";
        assertSuccess(minValidInput);
    }

    @Test
    public void defaultOmeroPort() {
        assertSuccess(minValidInput);
        assertThat(minValidInput.omeroPort,
                   is(OmeroDefault.Port.toString()));
        assertNotNull(validator.getOmero());
        assertThat(validator.getOmero().getPort(),
                   is(OmeroDefault.Port.get().intValue()));
    }

    @Test
    public void defaultKeepAlive() {
        assertSuccess(minValidInput);
        assertThat(minValidInput.keepAliveDuration,
                   is("" + OmeroDefault.SessionTimeout.toMillis()));
        assertNotNull(validator.getKeepAlive());
        assertThat(validator.getKeepAlive(),
                   is(OmeroDefault.SessionTimeout));
    }

}

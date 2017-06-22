package ome.smuggler.web.omero;

import static ome.smuggler.core.types.ValueParserFactory.*;
import static ome.smuggler.web.Error.error;
import static util.object.Either.right;
import static util.object.Eithers.collectLeft;
import static util.string.Strings.isNullOrEmpty;
import static util.string.Strings.unlines;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import ome.smuggler.core.types.*;
import ome.smuggler.web.Error;
import util.object.Either;
import util.validation.Validator;

/**
 * Validates a {@link CreateSessionRequest}.
 * Validation is carried out using field parsers to check whether it's possible
 * to instantiate valid values from the provided input fields and if the {@link
 * #validate(CreateSessionRequest) validate} method returns successfully (i.e.
 * right value) the parsed values will be available through the various getters
 * provided by this class.
 */
public class CreateSessionRequestValidator
        implements Validator<Error, CreateSessionRequest> {

    private CreateSessionRequest request;

    private Either<String, URI> omero;
    private Either<String, String> username;
    private Either<String, String> password;
    private Either<String, Duration> keepAlive;

    private List<Either<String, ?>> parseResults;


    private void applyDefaults(CreateSessionRequest r) {
        if (isNullOrEmpty(r.omeroPort)) {
            r.omeroPort = OmeroDefault.Port.toString();
        }
        if (isNullOrEmpty(r.keepAliveDuration)) {
            long timeout = OmeroDefault.SessionTimeout.toMillis();
            r.keepAliveDuration = Long.toString(timeout);
        }
    }

    private void checkRequiredFields(CreateSessionRequest r) {
        omero = label("omeroHost, omeroPort", omeroUri(r.omeroHost, r.omeroPort));
        username = label("username", string(r.username));
        password = label("password", string(r.password));
        keepAlive = label("keepAliveDuration", millis(r.keepAliveDuration));

        parseResults.addAll(Arrays.asList(omero, username, password, keepAlive));
    }

    private Optional<String> collectErrors() {
        String errors = unlines(collectLeft(parseResults.stream()));
        return isNullOrEmpty(errors) ? Optional.empty() : Optional.of(errors);
    }

    @Override
    public Either<Error, CreateSessionRequest> validate(CreateSessionRequest r) {
        request = r;
        if (r != null) {
            parseResults = new ArrayList<>();

            applyDefaults(r);
            checkRequiredFields(r);

            Optional<String> errors = collectErrors();
            return errors.isPresent() ? error(errors.get()) : right(r);
        }
        return error("no create session request");
    }

    // util getters to use *only* in case validation succeeds

    public CreateSessionRequest getRequest() {
        return request;
    }

    public URI getOmero() {
        return omero.getRight();
    }

    public String getUsername() {
        return username.getRight();
    }

    public String getPassword() {
        return password.getRight();
    }

    public Duration getKeepAlive() {
        return keepAlive.getRight();
    }

}

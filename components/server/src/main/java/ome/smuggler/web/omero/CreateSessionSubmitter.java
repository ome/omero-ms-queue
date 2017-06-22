package ome.smuggler.web.omero;

import static java.util.Objects.requireNonNull;
import static util.lambda.Functions.constant;

import java.util.Optional;

import ome.smuggler.core.service.omero.SessionService;
import ome.smuggler.web.Error;
import util.object.Either;


/**
 * Submits a request to create a session to the {@link SessionService OMERO
 * session service}.
 * This entails request validation, changing data format, and producing a
 * a response from the session key returned by the session service.
 */
public class CreateSessionSubmitter {

    public static final String CannotCreateSessionErrorMsg = "cannot create session";


    private final SessionService service;

    /**
     * Creates a new instance.
     * @param service the underlying OMERO session service.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public CreateSessionSubmitter(SessionService service) {
        requireNonNull(service, "service");
        this.service = service;
    }

    private Either<Error, CreateSessionRequestValidator> validate(
            CreateSessionRequest data) {
        CreateSessionRequestValidator v = new CreateSessionRequestValidator();
        return v.validate(data).map(constant(v));
    }

    private Either<Error, String> cannotCreateSessionError() {
        return Either.left(new Error(CannotCreateSessionErrorMsg));
    }

    private Either<Error, String> process(
            CreateSessionRequestValidator validRequest) {
        Optional<Either<Error, String>> result =
                service.createAndKeepAlive(validRequest.getOmero(),
                                           validRequest.getUsername(),
                                           validRequest.getPassword(),
                                           validRequest.getKeepAlive())
                       .map(Either::right);
        return result
               .orElse(cannotCreateSessionError());
    }

    private CreateSessionResponse toResponse(String sessionKey) {
        CreateSessionResponse r = new CreateSessionResponse();
        r.sessionKey = sessionKey;
        return r;
    }

    /**
     * Submits the given request to the underlying service.
     * First off, the given request data is {@link CreateSessionRequestValidator
     * validated}. If validation fails an {@link Error} will be returned that
     * details the detected validation errors. Otherwise, the request is
     * submitted to the underlying service. If the service cannot process the
     * request successfully, again an {@link Error} will be returned. Otherwise,
     * the data returned by the service is used to instantiate a {@link
     * CreateSessionResponse}.
     * @param data the request to submit.
     * @return either a create session response if the request could be
     * processed successfully or an error detailing what failed.
     */
    public Either<Error, CreateSessionResponse> submit(
            CreateSessionRequest data) {
        return validate(data)
               .bind(this::process)
               .map(this::toResponse);
    }

}

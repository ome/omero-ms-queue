package ome.smuggler.web.omero;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static util.spring.http.ResponseEntities.okOr400;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import ome.smuggler.core.service.omero.SessionService;
import ome.smuggler.web.Error;


/**
 * Exposes OMERO session-related operations.
 */
@RestController  // includes @ResponseBody: return vals bound to response body.
@RequestMapping(SessionController.CreateUrl)
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class SessionController {

    public static final String CreateUrl = "/omero/session/create";

    @Autowired
    private SessionService service;

    /**
     * Opens a new OMERO session.
     * <p>This method creates a fresh session for the user specified in the
     * {@link CreateSessionRequest request object} and keeps it alive for the
     * amount of time specified in the request object's keep-alive {@link
     * CreateSessionRequest#keepAliveDuration field}. The client can use the
     * session anytime before the specified amount of time elapses. Past that
     * time, if the client hasn't yet made any calls to the OMERO server using
     * this session, the session will be expired and no longer usable. Also
     * past that time, the client is responsible to keep the session alive, if
     * still needed. In any case, the client is responsible for closing the
     * session.
     * </p>
     * <p>This Web method can be useful when executing OMERO tasks on behalf of
     * a user at a future time point.
     * In such scenarios, the user asks some system (e.g. Smuggler) to execute
     * an OMERO task (e.g. an import) sometime later, after they have logged
     * out of OMERO. When the time comes to execute the task, the system in
     * question will need a valid OMERO session but where to get one from? One
     * option could be that the system provides its own OMERO logging form and
     * somehow gets hold of the user to make them log in just before executing
     * the task. This is often difficult or impractical. Another option could
     * be that the system stores the user's password, but this is not secure.
     * This Web method provides an alternative: create a session upfront and
     * keep it alive until the time to execute the task comes.
     * </p>
     * <p>Concerning security, it is also important to note that this method
     * requires the client to pass in the user's password, so the connection
     * should be properly secured using HTTPS. However, this Web method does
     * not store the password, it discards it just after creating a new OMERO
     * session.
     * </p>
     * <p>This method returns a {@code 200} HTTP message when all of the
     * following holds true:</p>
     * <ul>
     *     <li>the input request is not {@code null}; and</li>
     *     <li>is valid according to the fields specification of {@link
     *     CreateSessionRequest}; and</li>
     *     <li>contains valid user credentials; and</li>
     *     <li>a new OMERO session could be created with them.</li>
     * </ul>
     * <p>The body of the returned message is a JSON-serialised {@link
     * CreateSessionResponse}.
     * </p>
     * <p>This method returns a {@code 400} HTTP message when any of the
     * above conditions are not met. In this case, the body of the message is
     * a JSON-serialised instance of {@link Error} which details the failure.
     * </p>
     *
     * @param data details how to create a new session.
     * @return either an {@link Error} or a {@link CreateSessionResponse}.
     */
    @RequestMapping(method = POST,
                    consumes = MediaType.APPLICATION_JSON_VALUE,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> create(@RequestBody CreateSessionRequest data) {
        CreateSessionSubmitter submitter = new CreateSessionSubmitter(service);
        return okOr400(submitter.submit(data));
    }

}

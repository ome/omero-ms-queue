package end2end.web;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import ome.smuggler.web.Error;
import ome.smuggler.web.omero.CreateSessionRequest;
import ome.smuggler.web.omero.CreateSessionSubmitter;
import ome.smuggler.web.omero.SessionController;


public class CreateSessionTest extends BaseWebTest {

    public static CreateSessionRequest minValidRequest() {
        CreateSessionRequest minValidInput = new CreateSessionRequest();
        minValidInput.omeroHost = "omeroHost";
        minValidInput.username = "username";
        minValidInput.password = "password";

        return minValidInput;
    }

    /* curl -v -H 'Accept: application/json'
     *         -H 'Content-Type: application/json'
     *         -X POST -d '{}'
     *         http://localhost:8000/omero/session/create
     */
    @Test
    public void postRequestWithoutRequiredFields() {
        ResponseEntity<Error> response =
                post(url(SessionController.CreateUrl),
                     "{}",
                     Error.class);

        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody().reason, is(not(isEmptyOrNullString())));
    }

    /* curl -v -H 'Accept: application/json'
     *         -H 'Content-Type: application/json'
     *         -X POST -d ''
     *         http://localhost:8000/omero/session/create
     */
    @Test
    public void postRequestWithEmptyBody() {
        ResponseEntity<String> response =
                post(url(SessionController.CreateUrl),
                     "",
                     String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody(),
                   containsString("HttpMessageNotReadableException"));
    }
    /* NB Our controller is not even hit, Spring MVC handles this internally so
     * we can't return an instance of our Error class!
     */

    /* curl -v -H 'Accept: * / *'
     *         -H 'Content-Type: application/json'
     *         -X POST -d '{"omeroHost":"omeroHost",
     *                      "username":"username",
     *                      "password":"password"}'
     *         http://localhost:8000/omero/session/create
     */
    @Test
    public void postValidRequestWithAcceptAll() {
        ResponseEntity<String> response =
                post(url(SessionController.CreateUrl),
                     minValidRequest(),
                     String.class,
                     hs -> hs.add(HttpHeaders.ACCEPT, MediaType.ALL_VALUE));

        assertThat(response.getStatusCode(), is(HttpStatus.NOT_ACCEPTABLE));
    }
    /* NB this is weird, perhaps a Spring MVC bug! Our controller is not even
     * called, I'd have expected the same outcome as in postValidRequest().
     */

    /* curl -v -H 'Accept: application/json'
     *         -H 'Content-Type: application/json'
     *         -X POST -d '{"omeroHost":"omeroHost",
     *                      "username":"username",
     *                      "password":"password"}'
     *         http://localhost:8000/omero/session/create
     */
    @Test
    public void postValidRequestForWhichCannotCreateSession() {
        ResponseEntity<Error> response =
                post(url(SessionController.CreateUrl),
                     minValidRequest(),
                     Error.class);

        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody().reason,
                   containsString(
                           CreateSessionSubmitter.CannotCreateSessionErrorMsg));
    }

}

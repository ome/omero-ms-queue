package ome.smuggler.web.imports;

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

import ome.smuggler.core.service.imports.ImportRequestor;
import ome.smuggler.web.Error;

/**
 * Enqueues a message to request OMERO imports.
 */
@RestController  // includes @ResponseBody: return vals bound to response body.
@RequestMapping(ImportController.ImportUrl)
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class ImportController {

    public static final String ImportUrl = "/ome/import";

    @Autowired
    private ImportRequestor service;

    /**
     * Adds the given requests to the import queue.
     * <p>The requested imports will be queued and will be processed as soon
     * as resources are available; this method returns immediately so that
     * the client doesn't have to wait for the imports to complete.
     * </p>
     * <p>An outcome notification will be sent to the email address specified
     * in the requests. (For now we assume all requests have the same email
     * address, but going forward this method should work even with possibly
     * unrelated import requests.) This notification is just a one-liner success
     * message when all the submitted data are imported successfully into OMERO.
     * Otherwise, if some of the data failed to import, then a more detailed
     * message is sent that specifies which imports failed and which succeeded.
     * </p>
     * <p>
     * Each request must contain a valid OMERO session key that the client has
     * acquired before hand; the corresponding OMERO session will be used to
     * import the data and then closed by the server. The same session key may
     * be shared by any of the submitted requests.
     * </p>
     * <p>This method returns a {@code 200} HTTP message when:</p>
     * <ul>
     *  <li>The input array is {@code null} or empty or only contains {@code
     *  null} elements. In this case the body of the message is a JSON empty
     *  array.
     *  </li>
     *  <li>The input array contains some non-{@code null} requests and each of
     *  them is valid according to the fields specification of {@link
     *  ImportRequest}. (This method filters out any {@code null} element and
     *  only processes non-{@code null} objects.)
     *  The body of the returned message is a JSON-serialised array of {@link
     *  ImportResponse}s, one response for each (non-{@code null}) request.
     *  Note that the returned response objects may be in a different order
     *  than the one of the requests passed into this method's input array.
     *  </li>
     * </ul>
     * <p>This method returns a {@code 400} HTTP message when:</p>
     * <ul>
     *  <li>The input array contains some non-{@code null} requests but some
     *  of them fails validation. In this case, the body of the message is
     *  a JSON-serialised instance of {@link Error} which details the detected
     *  validation errors for each request that didn't pass validation.
     *  </li>
     * </ul>
     *
     * @param data details what image data to import.
     * @return either an {@link Error} or an array of {@link ImportResponse}s.
     */
    @RequestMapping(method = POST, 
                    consumes = MediaType.APPLICATION_JSON_VALUE,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> enqueue(@RequestBody ImportRequest[] data) {
        ImportBatchSubmitter submitter = new ImportBatchSubmitter(service);
        return okOr400(submitter.submit(data));
    }

}

package end2end.web;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static java.util.stream.Collectors.toSet;

import java.net.URI;
import java.util.Set;
import java.util.stream.Stream;

import ome.smuggler.web.Error;
import ome.smuggler.web.imports.ImportController;
import ome.smuggler.web.imports.ImportRequest;
import ome.smuggler.web.imports.ImportResponse;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;


public class EnqueueImportTest extends BaseWebTest {

    private static ImportRequest[] buildMinValidRequests(int howMany) {
        ImportRequest[] body = new ImportRequest[howMany];
        for (int k = 0; k < howMany; ++k) {
            body[k] = new ImportRequest();
            body[k].experimenterEmail = "x@y";
            body[k].targetUri = "my/file";
            body[k].omeroHost = "h";
            body[k].omeroPort = "1";
            body[k].sessionKey = "k";
        }
        return body;
    }

    private static void assert200ResponseBody(
            ImportResponse[] body, int expectedLength) {
        assertNotNull(body);
        assertThat(body.length, is(expectedLength));

        for (ImportResponse r : body) {
            assertNotNull(r);
            assertThat(r.statusUri, is(not(isEmptyOrNullString())));
            assertFalse(URI.create(r.statusUri).isAbsolute());
            assertThat(r.targetUri, is(not(isEmptyOrNullString())));
            assertTrue(URI.create(r.targetUri).isAbsolute());
        }
    }

    private <X> ImportResponse[] postAndAssert200Response(X validRequestBody) {
        ResponseEntity<ImportResponse[]> response =
                post(url(ImportController.ImportUrl),
                     validRequestBody,
                     ImportResponse[].class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        return response.getBody();
    }
    
    /* curl -v -H 'Accept: application/json' 
     *         -H 'Content-Type: application/json'  
     *         -X POST -d '[{"experimenterEmail": "x@y",
     *                       "targetUri":"my/file",
     *                       "omeroHost":"h",
     *                       "omeroPort":"1",
     *                       "sessionKey":"k"}]'
     *         http://localhost:8000/ome/import
     */
    @Test
    public void postOneImport() {
        ImportResponse[] responseBody =
                postAndAssert200Response(buildMinValidRequests(1));
        assert200ResponseBody(responseBody, 1);
    }

    /* curl -v -H 'Accept: application/json'
     *         -H 'Content-Type: application/json'
     *         -X POST -d '[{"experimenterEmail": "x@y",
     *                       "targetUri":"my/file",
     *                       "omeroHost":"h",
     *                       "omeroPort":"1",
     *                       "sessionKey":"k"}
     *                      ,
      *                      {"experimenterEmail": "x@y",
     *                       "targetUri":"my/file",
     *                       "omeroHost":"h",
     *                       "omeroPort":"1",
     *                       "sessionKey":"k"}]'
     *         http://localhost:8000/ome/import
     */
    @Test
    public void postManyImports() {
        ImportResponse[] responseBody =
                postAndAssert200Response(buildMinValidRequests(2));
        assert200ResponseBody(responseBody, 2);

        Set<String> statusUris = Stream.of(responseBody)
                                       .map(r -> r.statusUri)
                                       .collect(toSet());
        assertThat(statusUris.size(), is(2));
    }

    /* curl -v -H 'Accept: application/json'
     *         -H 'Content-Type: application/json'
     *         -X POST -d '[]'
     *         http://localhost:8000/ome/import
     */
    @Test
    public void postRequestWithEmptyArray() {
        ImportResponse[] importResponse = postAndAssert200Response("[]");
        assertThat(importResponse.length, is(0));
    }

    /* curl -v -H 'Accept: application/json'
     *         -H 'Content-Type: application/json'
     *         -X POST -d '[null]'
     *         http://localhost:8000/ome/import
     */
    @Test
    public void postRequestWithArrayContainingOneNullElement() {
        ImportResponse[] importResponse = postAndAssert200Response("[null]");
        assertThat(importResponse.length, is(0));
    }

    /* curl -v -H 'Accept: application/json'
     *         -H 'Content-Type: application/json'
     *         -X POST -d '[null, null]'
     *         http://localhost:8000/ome/import
     */
    @Test
    public void postRequestWithArrayContainingManyNullElements() {
        ImportResponse[] importResponse =
                postAndAssert200Response("[null, null]");
        assertThat(importResponse.length, is(0));
    }

    /* curl -v -H 'Accept: application/json' 
     *         -H 'Content-Type: application/json'  
     *         -X POST -d '[{}]'
     *         http://localhost:8000/ome/import
     */
    @Test
    public void postRequestWithoutRequiredFields() {
        ResponseEntity<Error> response = 
                post(url(ImportController.ImportUrl), 
                     "[{}]",
                     Error.class);
        
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody().reason, is(not(isEmptyOrNullString())));
    }
    
    /* curl -v -H 'Accept: application/json' 
     *         -H 'Content-Type: application/json' 
     *         -X POST -d '' 
     *         http://localhost:8000/ome/import
     */
    @Test
    public void postRequestWithEmptyBody() {
        ResponseEntity<String> response = 
                post(url(ImportController.ImportUrl), 
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
     *         -X POST -d '[{"experimenterEmail": "x@y",
     *                      "targetUri":"/", 
     *                      "omeroHost":"h", 
     *                      "omeroPort":"1", 
     *                      "sessionKey":"k"}]'
     *         http://localhost:8000/ome/import
     */
    @Test
    public void postValidRequestWithAcceptAll() {
        ResponseEntity<String> response = 
                post(url(ImportController.ImportUrl), 
                     buildMinValidRequests(1),
                     String.class,
                     hs -> hs.add(HttpHeaders.ACCEPT, MediaType.ALL_VALUE));
        
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_ACCEPTABLE));
    }
    /* NB this is weird, perhaps a Spring MVC bug! Our controller is not even 
     * called, I'd have expected the same outcome as in postValidRequest().
     */
}

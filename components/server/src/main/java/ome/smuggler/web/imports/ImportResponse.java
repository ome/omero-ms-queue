package ome.smuggler.web.imports;

/**
 * Web clients receive instances of this class in response to an import request.
 * This is just a data transfer object whose sole purpose is to facilitate the
 * transfer of information from the client to the server.
 */
public class ImportResponse {

    /**
     * The location where the client can retrieve status updates to find out
     * what is the current state of the requested import.
     * This URL will only be available to clients while the import is queued or
     * running and up until a configured amount of time after the import has 
     * completed (either with a success or a failure).  Past that time, the link
     * will not be available anymore and clients will receive a 404.
     */
    public String statusUri;

    /**
     * URI pointing to the data to import that was submitted in the {@link
     * ImportRequest}.
     */
    public String targetUri;
    
}

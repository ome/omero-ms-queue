package examples;

import java.net.URI;

import ome.smuggler.jclient.core.service.dto.imports.ImportRequest;
import ome.smuggler.jclient.frontend.Importer;
import ome.smuggler.jclient.frontend.ImporterConfig;

/**
 * Offloading of a simple import to a local Smuggler instance that connects to
 * a local OMERO server.
 */
public class SimpleImport implements Runnable {

    private ImporterConfig config() {
        URI sessionProxyBaseUrl = URI.create("http://localhost:8000");
        URI importProxyBaseUrl = URI.create("http://localhost:8000");
        String username = "tasty";
        String password = "*****";

        return new ImporterConfig(sessionProxyBaseUrl, importProxyBaseUrl,
                                  username, password);
    }

    private ImportRequest request() {
        ImportRequest request = new ImportRequest();
        request.experimenterEmail = "tasty.tests@igh.cnrs.fr";
        request.targetUri = "/home/tasty/test-images/SP8_40x_Cy5.tif";
        request.omeroHost = "localhost";

        return request;
    }

    private Importer importer() {
        return new Importer(config());
    }

    @Override
    public void run() {
        importer().handover(request());
    }

    public static void main(String[] args) {
        SimpleImport example = new SimpleImport();
        example.run();
    }

}

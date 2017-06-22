package ome.smuggler.web.imports;

import java.util.stream.IntStream;

public class Utils {

    public static ImportRequest newImportRequest(int sessionCounter) {
        ImportRequest minValidInput = new ImportRequest();
        minValidInput.experimenterEmail = "e@edu";
        minValidInput.targetUri = "/some/file";
        minValidInput.omeroHost = "omero";
        minValidInput.omeroPort = "1234";
        minValidInput.sessionKey = "sesh = " + sessionCounter;

        return minValidInput;
    }

    public static ImportRequest[] newImportRequests(int howMany) {
        return IntStream.range(0, howMany)
                        .mapToObj(Utils::newImportRequest)
                        .toArray(ImportRequest[]::new);
    }

}

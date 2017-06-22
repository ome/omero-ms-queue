package ome.smuggler.web.omero;

public class Utils {

    public static CreateSessionRequest minValidCreateSessionRequest() {
        CreateSessionRequest minValidInput = new CreateSessionRequest();
        minValidInput.omeroHost = "omeroHost";
        minValidInput.username = "username";
        minValidInput.password = "password";

        return minValidInput;
    }

}

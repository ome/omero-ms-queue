package ome.smuggler.run;

import static util.sequence.Arrayz.array;

import java.util.Arrays;
import java.util.List;

/**
 * Runs one of our applications in this package.
 */
public class AppRunner {

    private void usage() {
        System.out.println("To run any of the available apps:");
        System.out.println();
        System.out.println("\tjava -jar ome-smuggler-<n>.jar <fqcn> [args]");
        System.out.println();
        System.out.println("where:");
        System.out.println("\t'n' is the version number of the ome-smuggler jar");
        System.out.println("\t'fqcn' is the fully qualified class name of the app to run;");
        System.out.println("\t       it is expected to implement RunnableApp");
        System.out.println("\t'args' are any arguments to pass to the app");
    }
    
    private List<String> buildAppArgs(String[] args) {
        return Arrays.asList(args).subList(1, args.length);
    }
    
    private void invoke(String[] args) {
        RunnableApp app = null;
        try {
            app = (RunnableApp) Class.forName(args[0]).newInstance();   
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("No such RunnableApp: " + args[0]);
            usage();
        }
        if (app != null) {
            List<String> appArgs = buildAppArgs(args);
            app.run(appArgs);    
        }
    }
    
    /**
     * Runs the app with the given command line arguments.
     * If no arguments are given, we start the server with production settings.
     * @param args the arguments vector as given on the command line. 
     */
    public void launch(String[] args) {
        if (args == null || args.length == 0) {
            args = array(ImportServer.class.getName());
        }
        invoke(args);
    }
    
}

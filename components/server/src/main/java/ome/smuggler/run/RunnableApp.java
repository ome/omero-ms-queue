package ome.smuggler.run;

import java.util.List;

/**
 * All our apps in this package implement this interface so we can easily
 * run any of them; also they all are expected to have a parameter-less
 * constructor.
 * The command line to run any of them is supposed to look like:
 * <pre>
 * java -jar ome-smuggler-0.1.0.jar ome.smuggler.run.ChosenApp any app args
 * </pre>
 * where {@code ome.smuggler.run.ChosenApp} is the fully qualified name of a 
 * class implementing this interface and "{@code any app args}" are, well, 
 * exactly what you think: any arguments specific to the app to run.
 */
public interface RunnableApp {

    /**
     * Runs the app with the given command line arguments.
     * These are the arguments specific to the app, that is what follows the
     * fully qualified class name of the app on the command line.
     * @param appArgs the app arguments as given on the command line. 
     */
    void run(List<String> appArgs);
    
}

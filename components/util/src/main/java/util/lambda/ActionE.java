package util.lambda;

/**
 * An action that may throw an exception when run.
 */
@FunctionalInterface
public interface ActionE {

    void run() throws Exception;
    
}

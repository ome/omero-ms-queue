package util.spring.io;

/**
 * Signals that a resource could not be read into an object because of some
 * I/O or data conversion error.
 */
public class ResourceReadException extends RuntimeException {

    private static final long serialVersionUID = 480350109931069666L;
 
    /**
     * Creates a new instance to wrap the given exception.
     * @param e the cause of the error.
     */
    public ResourceReadException(Throwable e) {
        super(e);
    }
    
}

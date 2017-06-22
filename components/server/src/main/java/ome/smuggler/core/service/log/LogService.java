package ome.smuggler.core.service.log;

import java.io.PrintWriter;
import java.util.function.Consumer;

/**
 * Defines the functionality that our services need to log messages.
 * All methods have to make sure that if an exception occurs when writing the
 * log message to the target logging facility, then it is swallowed and printed
 * to {@code stderr}.
 */
public interface LogService {

    /**
     * Logs a debug message. 
     * @param site where the event occurred or the object the event is 
     * associated to.
     * @param messageWriter writes the content of the log message.
     * @throws NullPointerException if any argument is {@code null}.
     */
    void debug(Object site, Consumer<PrintWriter> messageWriter);
    
    /**
     * Logs an informational message. 
     * @param site where the event occurred or the object the event is 
     * associated to.
     * @param messageWriter writes the content of the log message.
     * @throws NullPointerException if any argument is {@code null}.
     */
    void info(Object site, Consumer<PrintWriter> messageWriter);
    
    /**
     * Logs a warning. 
     * @param site where the event occurred or the object the event is 
     * associated to.
     * @param messageWriter writes the content of the log message.
     * @throws NullPointerException if any argument is {@code null}.
     */
    void warn(Object site, Consumer<PrintWriter> messageWriter);
    
    /**
     * Logs an error. 
     * @param site where the event occurred or the object the event is 
     * associated to.
     * @param messageWriter writes the content of the log message.
     * @throws NullPointerException if any argument is {@code null}.
     */
    void error(Object site, Consumer<PrintWriter> messageWriter);
    
}

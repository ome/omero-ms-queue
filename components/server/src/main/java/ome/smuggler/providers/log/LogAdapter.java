package ome.smuggler.providers.log;

import static util.string.Strings.write;

import java.io.PrintWriter;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ome.smuggler.core.service.log.LogService;

public class LogAdapter implements LogService {

    /**
     * Gets a logger for the given object.
     * @param x the target object.
     * @return the logger.
     * @throws NullPointerException if the argument is {@code null}.
     */
    private static Logger loggerFor(Object x) {
        return LoggerFactory.getLogger(x.getClass().getName());
    }

    @Override
    public void debug(Object site, Consumer<PrintWriter> messageWriter) {
        loggerFor(site).debug(write(messageWriter));
    }

    @Override
    public void info(Object site, Consumer<PrintWriter> messageWriter) {
        loggerFor(site).info(write(messageWriter));
    }

    @Override
    public void warn(Object site, Consumer<PrintWriter> messageWriter) {
        loggerFor(site).warn(write(messageWriter));
    }

    @Override
    public void error(Object site, Consumer<PrintWriter> messageWriter) {
        loggerFor(site).error(write(messageWriter));
    }

}

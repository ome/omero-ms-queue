package ome.smuggler.core.service.log;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static util.error.Exceptions.runAndSwallow;
import static util.sequence.Streams.pruneNull;

import java.io.PrintWriter;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import util.object.Pair;
import util.sequence.Streams;

/**
 * Base class for loggers that extend the basic functionality of the 
 * {@link LogService} to provide logging tailored to a particular service.
 */
public class BaseLogger implements LogService {

    /**
     * Prints the given name-value pairs, one per line, into a string.
     * Each line has the format: "{@code >>> name: value}".
     * @param fields the pairs to print.
     * @return a string containing the printed pairs.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static String fieldsToString(Stream<Pair<Object, Object>> fields) {
        requireNonNull(fields, "fields");
        
        return fields
            .map(p -> String.format(">>> %s: %s%n", p.fst(), p.snd()))
            .collect(joining());
    }
    
    /**
     * Builds a consumer that writes the given name-value pairs to a 
     * {@link PrintWriter} buffer using the {@link #fieldsToString(Stream) 
     * fieldsToString} method.
     * @param header the first line to output.
     * @param fields the name-value pairs to output.
     * @return the consumer.
     * @throws NullPointerException if any argument is {@code null}.
     */
    @SafeVarargs
    public static Consumer<PrintWriter> fieldsWriter(String header,
            Stream<Pair<Object, Object>>...fields) {
        requireNonNull(header, "header");

        return buf -> {
            buf.println(header);
            Stream<Pair<Object, Object>> fs = Streams.concat(fields);
            buf.println(fieldsToString(fs));
        };
    }


    private final LogService service;

    /**
     * Creates a new instance.
     * @param service the underlying log service.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public BaseLogger(LogService service) {
        requireNonNull(service, "service");
        this.service = service;
    }

    private void log(BiConsumer<Object, Consumer<PrintWriter>> logger,
                     Object site,
                     Consumer<PrintWriter> messageWriter) {
        requireNonNull(logger, "logger");
        requireNonNull(site, "site");
        requireNonNull(messageWriter, "messageWriter");

        try {
            logger.accept(site, messageWriter);
        } catch (Throwable t) {
            runAndSwallow(() -> {
                System.err.println("unexpected exception while logging:");
                System.err.println(t.toString());
                t.printStackTrace(System.err);
            });
        }
    }

    @Override
    public void debug(Object site, Consumer<PrintWriter> messageWriter) {
        log(service::debug, site, messageWriter);
    }
    
    @Override
    public void info(Object site, Consumer<PrintWriter> messageWriter) {
        log(service::info, site, messageWriter);
    }

    @Override
    public void warn(Object site, Consumer<PrintWriter> messageWriter) {
        log(service::warn, site, messageWriter);
    }

    @Override
    public void error(Object site, Consumer<PrintWriter> messageWriter) {
        log(service::error, site, messageWriter);
    }

    /**
     * Logs a warning containing a summary of the error and a debug message
     * with the entire exception's stack trace.
     * @param site where the error was detected.
     * @param error the error that was caught.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public void transientError(Object site, Throwable error) {
        requireNonNull(error, "error");
        
        warn(site, buf -> buf.print(error.toString()));
        debug(site, error::printStackTrace);
    }

    /**
     * Logs a warning containing a summary of the error and a debug message
     * with the entire exception's stack trace for each non-empty optional
     * in the list.
     * @param site where the error was detected.
     * @param maybeE errors that were caught, possibly none. If the array is
     *               {@code null}, nothing is logged. Any {@code null} or
     *               empty element will be skipped.
     * @throws NullPointerException if the site argument is {@code null}.
     */
    @SafeVarargs
    final public void transientError(Object site, Optional<Throwable>...maybeE) {
        requireNonNull(site, "site");

        pruneNull(maybeE).filter(Optional::isPresent)
                         .map(Optional::get)
                         .forEach(t -> transientError(site, t));
    }

}

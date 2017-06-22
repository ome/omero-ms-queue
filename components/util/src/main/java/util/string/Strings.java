package util.string;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static util.sequence.Arrayz.isNullOrZeroLength;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * String utilities
 */
public class Strings {

    /**
     * Is this a {@code null} or empty string?
     * @param x the string to test.
     * @return {@code true} for yes, {@code false} for no. 
     */
    public static boolean isNullOrEmpty(String x) {
        return x == null || x.isEmpty();
    }
    
    /**
     * Makes the given string an {@link Optional}.  
     * @param x the string to convert.
     * @return empty if the argument is {@code null} or empty; otherwise the
     * argument wrapped into an {@link Optional}.
     */
    public static Optional<String> asOptional(String x) {
        return isNullOrEmpty(x) ? Optional.empty() : Optional.of(x);
    }
    
    /**
     * Gives a {@link Consumer} a {@link PrintWriter} to write some text to.
     * @param writer the consumer that will produce the text.
     * @return the text produced by the consumer.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static String write(Consumer<PrintWriter> writer) {
        requireNonNull(writer, "writer");
        
        StringWriter buffer = new StringWriter();
        PrintWriter out = new PrintWriter(buffer);
        
        writer.accept(out);
        out.flush();
        
        return buffer.toString();
    }

    /**
     * Gives a {@link Consumer} a {@link PrintWriter} to write some text to the
     * specified destination {@link OutputStream}.
     * @param destination where the data will be written to.
     * @param writer the consumer that will produce the text.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static void write(OutputStream destination,
                             Consumer<PrintWriter> writer) {
        requireNonNull(destination, "destination");
        requireNonNull(writer, "writer");

        PrintWriter out = new PrintWriter(destination);
        writer.accept(out);
        out.flush();
    }

    /**
     * Writes the given content to the specified destination stream.
     * This method uses the {@link PrintWriter#print(String) PrintWriter's
     * print} method to write the content string. In particular the {@code null}
     * string is output as "null".
     * @param destination where the data will be written to.
     * @param content the data to write; if {@code null} or empty nothing will
     *                be written to the destination steam.
     * @throws NullPointerException if the destination argument is {@code null}.
     */
    public static void write(OutputStream destination, String content) {
        requireNonNull(destination, "destination");
        write(destination, writer -> writer.print(content));
    }
    
    /**
     * Throws an exception if the given string is {@link #isNullOrEmpty(String) 
     * null or empty}.
     * @param x the string to test.
     * @param message optional exception message; ignored if {@code null} or
     * empty.
     * @throws IllegalArgumentException if {@code x} is {@code null} or empty.
     */
    public static void requireString(String x, String message) {
        if (isNullOrEmpty(x)) {
            if(isNullOrEmpty(message)) throw new IllegalArgumentException();
            throw new IllegalArgumentException(message);
        }
    }
    
    /**
     * Throws an exception if the given string is {@link #isNullOrEmpty(String) 
     * null or empty}.
     * @param x the string to test.
     * @throws IllegalArgumentException if {@code x} is {@code null} or empty.
     */
    public static void requireString(String x) {
        requireString(x, null);
    }
    
    /**
     * Throws an exception if the argument is {@code null} or has zero length
     * or any of the elements is {@code null} or empty.
     * @param xs the array to test.
     * @throws IllegalArgumentException if the argument is {@code null} or has
     * zero length or any of the components is {@code null} or empty.
     */
    public static void requireStrings(String[] xs) {
        if (isNullOrZeroLength(xs)) {
            throw new IllegalArgumentException("no strings");
        }
        for (int k = 0; k < xs.length; ++k) {
            requireString(xs[k], "null or empty at " + k);
        }
    }
    
    /**
     * Reads the whole input into a UTF-8 string.
     * @param source the data to read; the stream will be closed when this 
     * method returns, even if an exception is thrown.
     * @return the whole content of the stream as a string.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws IOException if an error occurs while reading the stream.
     */
    public static String readAsString(InputStream source) throws IOException {
        requireNonNull(source, "source");
        
        InputStreamReader in = new InputStreamReader(source, 
                                                     StandardCharsets.UTF_8);
        return readAsString(in);
    }
    
    /**
     * Reads the whole input into a UTF-8 string.
     * @param source the data to read; the source will be closed when this 
     * method returns, even if an exception is thrown.
     * @return the whole input data provided by the source as a string.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws IOException if an error occurs while reading from the source.
     */
    public static String readAsString(Readable source) throws IOException {
        requireNonNull(source, "source");

        try (Scanner reader = new Scanner(source)) {
            reader.useDelimiter("\\A");
            String input = reader.hasNext() ? reader.next() : "";
            IOException maybeError = reader.ioException();
            if (maybeError != null) {
                throw maybeError;
            }
            return input;
        }
    }
    /* Adapted from:
     * - https://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html
     */
    
    /**
     * Breaks up the given text into lines.
     * @param text the input text.
     * @return the sequence of lines making up text.
     * @throws NullPointerException if the argument is {@code null} .
     */
    public static Stream<String> lines(String text) {
        requireNonNull(text, "text");
        
        Scanner reader = new Scanner(text);
        List<String> lines = new ArrayList<>();
        while (reader.hasNextLine()) {
            lines.add(reader.nextLine());
        }
        reader.close();
        
        return lines.stream();
    }
    
    /**
     * Appends a new line separator to each stream elements and then joins them
     * into a single string.
     * Any {@code null} or empty stream element will result in an empty line.
     * @param lines the lines to join.
     * @return the lines separated by a new line character into a single string.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static String unlines(Stream<String> lines) {
        requireNonNull(lines, "lines");
        return lines.map(x -> x == null ? "" : x)
                    .map(x -> String.format("%s%n", x))
                    .collect(joining());
    }
    
}

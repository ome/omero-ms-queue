package util.runtime;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

import util.object.Pair;

/**
 * Utility to run an external process redirecting both its error and output
 * streams to a file.
 */
public class CommandRunner {

    private static List<String> commandLine(CommandBuilder command) {
        return command.tokens().collect(toList());
    }
    
    private static ProcessBuilder processBuilder(CommandBuilder command) {
        return new ProcessBuilder(commandLine(command));
    }
    
    private final CommandBuilder command;
    
    /**
     * Creates a new instance to run a given process.
     * @param command the command to start the process.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public CommandRunner(CommandBuilder command) {
        requireNonNull(command, "command");
        this.command = command;
    }
    
    private Process startProcess(Redirect outputDestination) throws IOException {
        ProcessBuilder builder = processBuilder(command);
        builder.redirectErrorStream(true);
        builder.redirectOutput(outputDestination);
        
        Process process = builder.start();
        process.getOutputStream().close();
        
        return process;
    }
    
    /**
     * Runs the process redirecting both its error and output streams to the
     * specified file.
     * @param outputFile the file to redirect the process's streams to.
     * @return the process's exit status.
     * @throws IOException if an I/O error occurs while running the process.
     * @throws InterruptedException if the current thread is interrupted before 
     * the spawned process terminates.
     */
    public int exec(Path outputFile) throws IOException, InterruptedException {
        requireNonNull(outputFile, "outputFile");

        Redirect withOutputFile = Redirect.appendTo(outputFile.toFile());
        return startProcess(withOutputFile).waitFor();
    }

    /**
     * Runs the process redirecting both its error and output streams to an
     * input stream that is given to a consumer to read.
     * @param <T> any type.
     * @param outputReader reads the process's output.
     * @return the process's exit status and the output result as read by the
     * output reader.
     * @throws IOException if an I/O error occurs while running the process.
     * @throws InterruptedException if the current thread is interrupted before
     * the spawned process terminates.
     */
    public <T> Pair<Integer, T> exec(Function<InputStream, T> outputReader)
            throws IOException, InterruptedException {
        requireNonNull(outputReader, "outputReader");

        Process p = startProcess(Redirect.PIPE);
        T result = outputReader.apply(p.getInputStream());
        int status = p.waitFor();
        return new Pair<>(status, result);
    }
    
}

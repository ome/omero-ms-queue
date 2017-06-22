package ome.smuggler.core.service.omero.impl;

import ome.smuggler.core.io.CommandRunner;
import ome.smuggler.core.io.StreamOps;
import util.lambda.FunctionE;
import util.object.Pair;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;

import static util.object.Pair.pair;
import static java.util.Objects.requireNonNull;

/**
 * Spawns a separate JVM process to run an OME CLI command.
 */
public class OmeCliCommandRunner {

    private static boolean succeeded(int status) {
        return status == 0;
    }

    private final OmeroEnv env;
    private final OmeCliCommandBuilder cmd;

    /**
     * Creates a new instance to run the specified command.
     * @param env the service environment.
     * @param cmd the command to run.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public OmeCliCommandRunner(OmeroEnv env, OmeCliCommandBuilder cmd) {
        requireNonNull(env, "env");
        requireNonNull(cmd, "cmd");

        this.env = env;
        this.cmd = cmd;
    }

    private void logFailure(Pair<Integer, Path> outcome) {
        env.log().failure(cmd, outcome.fst(), outcome.snd());
    }

    private void logFailureWithOutput(Pair<Integer, String> outcome) {
        env.log().failure(cmd, outcome.fst(), outcome.snd());
    }

    private <T> Optional<T> doRun(
            FunctionE<CommandRunner, Pair<Integer, T>> spawn,
            Consumer<Pair<Integer, T>> cmdFailureLogger) {
        int status = -1;
        T result = null;
        CommandRunner runner = new CommandRunner(cmd);
        try {
            Pair<Integer, T> outcome = spawn.apply(runner);
            status = outcome.fst();
            result = outcome.snd();

            if (succeeded(status)) {
                env.log().success(cmd, status);
            } else {
                cmdFailureLogger.accept(outcome);
            }
        } catch (Exception e) {
            env.log().failure(cmd, e);
        }
        return succeeded(status) ? Optional.ofNullable(result)
                                 : Optional.empty();
    }

    /**
     * Runs the command redirecting all output to the specified file.
     * @param outputFile where to redirect output.
     * @return {@code true} if the command succeeded, {@code false} otherwise.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public boolean run(Path outputFile) {
        return doRun(runner -> {
            int status = runner.exec(outputFile);
            return pair(status, outputFile);
        }, this::logFailure)
        .isPresent();
    }

    /**
     * Runs the command discarding any output.
     * @return {@code true} if the command succeeded, {@code false} otherwise.
     */
    public boolean run() {
        return runAndCollectOutput().isPresent();
    }

    /**
     * Runs the command collecting any output in a result string.
     * @return the result string if the command succeeded, empty otherwise.
     */
    public Optional<String> runAndCollectOutput() {
        return doRun(runner -> runner.exec(StreamOps::readLinesIntoString),
                     this::logFailureWithOutput);
    }

}

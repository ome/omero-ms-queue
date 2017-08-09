package ome.smuggler.core.service.omero.impl;

import static java.util.Objects.requireNonNull;

import java.net.URI;
import java.time.Duration;
import java.util.Optional;

import ome.smuggler.core.service.omero.SessionService;
import ome.smuggler.core.types.QueuedOmeroKeepAlive;
import util.types.FutureTimepoint;

/**
 * Implements the {@link SessionService}.
 */
public class SessionManager implements SessionService {

    private final OmeroEnv env;

    /**
     * Creates a new instance.
     * @param env the service environment.
     */
    public SessionManager(OmeroEnv env) {
        requireNonNull(env, "env");

        this.env = env;
    }

    private String enqueueFirstKeepAlive(URI omeroHostAndPort,
                                         String sessionKey,
                                         Duration howLong) {
        FutureTimepoint whenToStopKeepAlive = new FutureTimepoint(howLong);
        QueuedOmeroKeepAlive msg = new QueuedOmeroKeepAlive(
                omeroHostAndPort,
                sessionKey,
                whenToStopKeepAlive);
        env.sessionQ().uncheckedSend(msg);

        return sessionKey;
    }

    @Override
    public Optional<String> create(URI omeroHostAndPort,
                                   String username, String password) {
        CreateCommandBuilder cmd =
                new CreateCommandBuilder(env.config(), omeroHostAndPort,
                                         username, password);
        OmeCliCommandRunner runner = new OmeCliCommandRunner(env, cmd);
        return runner.runAndCollectOutput();
    }

    @Override
    public boolean keepAlive(URI omeroHostAndPort, String sessionKey) {
        KeepAliveCommandBuilder cmd =
                new KeepAliveCommandBuilder(env.config(),
                        omeroHostAndPort, sessionKey);
        OmeCliCommandRunner runner = new OmeCliCommandRunner(env, cmd);
        return runner.run();
    }

    @Override
    public boolean close(URI omeroHostAndPort, String sessionKey) {
        CloseCommandBuilder cmd = new CloseCommandBuilder(env.config(),
                        omeroHostAndPort, sessionKey);
        OmeCliCommandRunner runner = new OmeCliCommandRunner(env, cmd);
        return runner.run();
    }

    @Override
    public Optional<String> createAndKeepAlive(URI omeroHostAndPort,
                                               String username,
                                               String password,
                                               Duration howLong) {

        return create(omeroHostAndPort, username, password)
               .map(sessionKey -> enqueueFirstKeepAlive(omeroHostAndPort,
                                                        sessionKey,
                                                        howLong));
    }

}

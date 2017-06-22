package ome.smuggler.core.service.imports.impl;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;
import static util.error.Exceptions.runAndCatch;
import static util.object.Pair.pair;

import java.net.URI;
import java.util.Optional;

import ome.smuggler.core.service.omero.SessionService;
import ome.smuggler.core.types.ImportBatch;
import ome.smuggler.core.types.QueuedImport;
import util.lambda.ActionE;
import util.object.Pair;

/**
 * Closes all the OMERO sessions in an import batch.
 */
public class BatchSessionsReaper {

    private final SessionService session;

    /**
     * Creates a new instance.
     * @param session the service to access OMERO sessions.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public BatchSessionsReaper(SessionService session) {
        requireNonNull(session, "session");
        this.session = session;
    }

    private Pair<URI, String> hostAndSessionKey(QueuedImport task) {
        return pair(task.getRequest().getOmeroHost(),
                    task.getRequest().getSessionKey());
    }

    private ActionE closeSession(Pair<URI, String> hostAndSessionKey) {
        return () -> session.close(hostAndSessionKey.fst(),
                                   hostAndSessionKey.snd());
    }

    /**
     * Closes all the OMERO sessions in the specified import batch.
     * @param batch the import batch.
     * @return any exception raised while closing sessions.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public Optional<Throwable>[] closeSessions(ImportBatch batch) {
        requireNonNull(batch, "batch");

        ActionE[] closeActions = batch.imports()
                                      .map(this::hostAndSessionKey)
                                      .collect(toSet())
                                      .stream()
                                      .map(this::closeSession)
                                      .toArray(ActionE[]::new);

        return runAndCatch(closeActions);
    }

}

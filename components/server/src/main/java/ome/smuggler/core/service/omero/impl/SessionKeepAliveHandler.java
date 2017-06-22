package ome.smuggler.core.service.omero.impl;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import ome.smuggler.core.msg.CountedSchedule;
import ome.smuggler.core.msg.Reschedulable;
import ome.smuggler.core.service.omero.SessionService;
import ome.smuggler.core.types.FutureTimepoint;
import ome.smuggler.core.types.QueuedOmeroKeepAlive;
import ome.smuggler.core.types.Schedule;


/**
 * Consumes a message from the session keep-alive queue by pinging the
 * specified OMERO session. If the ping command succeeds, a new keep-alive
 * is scheduled; otherwise the keep-alive cycle for the given session stops.
 */
public class SessionKeepAliveHandler
        implements Reschedulable<QueuedOmeroKeepAlive> {

    private final OmeroEnv env;
    private final SessionService service;

    /**
     * Creates a new instance.
     * @param env the OMERO environment.
     * @param service the session service.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public SessionKeepAliveHandler(OmeroEnv env, SessionService service) {
        requireNonNull(env, "env");
        requireNonNull(service, "service");

        this.env = env;
        this.service = service;
    }

    private Optional<QueuedOmeroKeepAlive> ping(QueuedOmeroKeepAlive data) {
        boolean succeeded =
                service.keepAlive(data.getOmero(), data.getSessionKey());

        return succeeded ? Optional.of(data) : Optional.empty();
    }

    private Optional<QueuedOmeroKeepAlive> checkNotReachedEndTimepoint(
            QueuedOmeroKeepAlive data) {
        return data.getUntilWhen().isStillInTheFuture() ? Optional.of(data)
                                                        : Optional.empty();
    }

    private Schedule<QueuedOmeroKeepAlive> nextSchedule(
            QueuedOmeroKeepAlive data) {
        FutureTimepoint when =
                new FutureTimepoint(env.config().sessionKeepAliveInterval());
        return new Schedule<>(when, data);
    }

    @Override
    public Optional<Schedule<QueuedOmeroKeepAlive>> consume(
            CountedSchedule current, QueuedOmeroKeepAlive data) {
        return ping(data)
               .flatMap(this::checkNotReachedEndTimepoint)
               .map(this::nextSchedule);
    }

}

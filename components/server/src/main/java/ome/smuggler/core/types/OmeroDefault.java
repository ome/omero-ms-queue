package ome.smuggler.core.types;

import java.time.Duration;

/**
 * Default values for OMERO configuration items.
 */
public class OmeroDefault {

    /**
     * Default OMERO server port: 4064.
     */
    public static final PositiveN Port = PositiveN.of(4064);

    /**
     * Default OMERO session timeout: 10 minutes.
     */
    public static final Duration SessionTimeout = Duration.ofMinutes(10);

    /**
     * Default OMERO session keep-alive interval: half of the {@link
     * #SessionTimeout default session timeout}. This is the value that
     * most OMERO clients use by default.
     */
    public static final Duration SessionKeepAliveInterval =
            SessionTimeout.dividedBy(2);

}

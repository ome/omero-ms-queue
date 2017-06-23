package util.network;

import java.net.InetAddress;
import java.util.Optional;

import util.lambda.SupplierE;


/**
 * Host name lookup utility.
 */
public class Hostname {

    /**
     * Default string used in place of a host name if the lookup fails.
     */
    public static final String UnknownHost = "[unknown host]";

    /**
     * Tries to look up the host name using the specified look up function.
     * @param f the lookup function.
     * @return either the output of the lookup function or empty if the
     * function throws an exception.
     */
    public static Optional<String> lookup(SupplierE<String> f) {
        try {
            String name = f.get();
            return Optional.of(name);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Tries to look up the host name, returning a generic {@link #UnknownHost}
     * string if the lookup fails. The lookup is performed using
     * {@link InetAddress}.
     * @return either the actual host name or {@link #UnknownHost}.
     */
    public static String lookup() {
        return lookup(() -> InetAddress.getLocalHost().getHostName())
                     .orElse(UnknownHost);
    }

}

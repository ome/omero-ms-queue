package ome.cli.cmd;

import java.util.Optional;
import java.util.stream.Stream;

import Glacier2.CannotCreateSessionException;
import Glacier2.PermissionDeniedException;
import Ice.ConnectionRefusedException;
import Ice.DNSException;

/**
 * Enumerates the exit codes for session-related commands.
 * For the lack of better standards, we adopt the Bash recommendations and
 * use {@code 0} for success and the range {@code 64 - 113} for errors. (This
 * happens to play well with C/C++ conventions too.) We reserve {@code 64} for
 * a parse error in {@code ome.cli.Main} when the wrong command name or no
 * arguments are given.
 *
 * @see <a href="http://tldp.org/LDP/abs/html/exitcodes.html">Bash exit codes</a>
 */
public enum ExitCode {
    
    Ok(0),
    InvalidArgs(65),
    InvalidPort(66),
    BadHostName(67, DNSException.class),
    ConnectionRefused(68, ConnectionRefusedException.class),
    CannotCreateSession(69, CannotCreateSessionException.class),
    PermissionDenied(70, PermissionDeniedException.class),
    ServerError(71, omero.ServerError.class),
    InternalError(72);

    private final int code;
    private final Optional<Class<?>> errorType;
    
    ExitCode(int code) {
        this.code = code;
        this.errorType = Optional.empty();
    }
    
    ExitCode(int code, Class<?> e) {
        this.code = code;
        this.errorType = Optional.of(e);
    }

    public int code() {
        return code;
    }
    
    public static ExitCode codeFor(Exception e) {
        if (e == null) {
            return InternalError;
        }
        return Stream.of(ExitCode.values())
                     .filter(x -> x.errorType.isPresent())
                     .filter(x -> x.errorType.get().equals(e.getClass()))
                     .findFirst()
                     .orElse(InternalError);
    }
    
}

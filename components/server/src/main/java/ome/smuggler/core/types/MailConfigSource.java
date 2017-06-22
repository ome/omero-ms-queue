package ome.smuggler.core.types;

import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import ome.smuggler.config.items.MailConfig;

/**
 * Provides read-only, type-safe access to mail configuration.
 * @see MailConfig
 */
public interface MailConfigSource {

    /**
     * @return the "from" address of each and every mail we send.
     */
    Email fromAddress();
    
    /**
     * @return the host and port of the mail server.
     */
    URI mailServer();
    
    /**
     * @return the protocol to use to communicate with the mail server.
     */
    MailProtocol protocol();
    
    /**
     * @return whether to skip certificate validation when SMTPS is in use;
     * always returns {@code false} if the protocol is not SMTPS.
     */
    boolean skipServerCertificateValidation();
    
    /**
     * @return the username for logging into the mail server, if configured.
     */
    Optional<String> username();
    
    /**
     * @return the password for logging into the mail server, if configured.
     */
    Optional<String> password();
    
    /**
     * @return intervals at which to retry failed mail relays.
     */
    List<Duration> retryIntervals();
    
    /**
     * @return path to the directory where to keep email messages that could not
     * be relayed to the mail server.
     */
    Path deadMailDir();
    
    /**
     * @return the email address of the system administrator, if configured or
     * empty otherwise.
     */
    Optional<Email> sysAdminAddress();
    
}

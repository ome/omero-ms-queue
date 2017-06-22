package ome.smuggler.core.types;

import static java.util.function.Function.identity;
import static java.util.Objects.requireNonNull;
import static ome.smuggler.core.convert.RawConfigValues.toDurationList;
import static ome.smuggler.core.convert.RawConfigValues.toURI;
import static ome.smuggler.core.types.MailProtocol.smtp;
import static ome.smuggler.core.types.MailProtocol.smtps;
import static ome.smuggler.core.types.ValueParserFactory.email;
import static util.string.Strings.asOptional;
import static util.string.Strings.isNullOrEmpty;

import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import ome.smuggler.config.BaseDataDir;
import ome.smuggler.config.items.MailConfig;

/**
 * Implements {@link MailConfigSource} by extracting and validating values 
 * from an underlying {@link MailConfig}.
 */
public class MailConfigReader implements MailConfigSource {

    private static Email parseEmail(String address) {
        return email(address)
              .either(errorMsg -> {
                          throw new IllegalArgumentException(errorMsg);
                      }, identity());
    }
    
    private static Optional<Email> parseOptionalEmail(String address) {
        return isNullOrEmpty(address) ? Optional.empty()
                                      : Optional.of(parseEmail(address));
    }
    
    private final Email fromAddress;
    private final URI mailServer;
    private final MailProtocol protocol;
    private final boolean skipServerCertificateValidation;
    private final List<Duration> retryIntervals;
    private final Path deadMailDir;
    private final Optional<String> username;
    private final Optional<String> password;
    private final Optional<Email> sysAdminAddress;
    
    /**
     * Creates a new instance.
     * @param config the configuration as obtained by the configuration 
     * provider.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public MailConfigReader(MailConfig config) {
        requireNonNull(config, "config");
        
        BaseDataDir base = new BaseDataDir();
        
        fromAddress = parseEmail(config.getFromAddress());
        mailServer = toURI("smtp", config.getMailServerHost(), 
                            config.getMailServerPort());
        retryIntervals = toDurationList(config.getRetryIntervals());
        deadMailDir = base.resolveRequiredPath(config.getDeadMailDir());
        username = asOptional(config.getUsername());
        password = asOptional(config.getPassword());
        protocol = config.getUseSmtps() ? smtps : smtp;
        skipServerCertificateValidation = smtps.equals(protocol) &&
                                    config.getSkipServerCertificateValidation();
        sysAdminAddress = parseOptionalEmail(config.getSysAdminEmail());
    }
    
    @Override
    public Email fromAddress() {
        return fromAddress;
    }

    @Override
    public URI mailServer() {
        return mailServer;
    }

    @Override
    public List<Duration> retryIntervals() {
        return retryIntervals;
    }

    @Override
    public Path deadMailDir() {
        return deadMailDir;
    }

    @Override
    public Optional<String> username() {
        return username;
    }

    @Override
    public Optional<String> password() {
        return password;
    }

    @Override
    public MailProtocol protocol() {
        return protocol;
    }

    @Override
    public boolean skipServerCertificateValidation() {
        return skipServerCertificateValidation;
    }

    @Override
    public Optional<Email> sysAdminAddress() {
        return sysAdminAddress;
    }

}

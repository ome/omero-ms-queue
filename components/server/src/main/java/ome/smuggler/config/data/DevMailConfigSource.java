package ome.smuggler.config.data;

import static ome.smuggler.core.types.ValueParserFactory.email;

import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import ome.smuggler.config.BaseDataDir;
import ome.smuggler.core.types.MailConfigSource;
import ome.smuggler.core.types.MailProtocol;
import util.types.Email;

/**
 * Dev mail settings.
 */
public class DevMailConfigSource implements MailConfigSource {

    private final BaseDataDir baseDataDir;
    
    public DevMailConfigSource() {
        this.baseDataDir = new BaseDataDir();
    }
    
    @Override
    public Email fromAddress() {
        return email("noreply@openmicroscopy.org").getRight();
    }

    @Override
    public URI mailServer() {
        return URI.create("smtp://localhost:25");
    }

    @Override
    public List<Duration> retryIntervals() {
        return Collections.emptyList();
    }

    @Override
    public Path deadMailDir() {
        return baseDataDir.resolve(MailYmlFile.DeadMailDirRelPath);
    }

    @Override
    public Optional<String> username() {
        return Optional.empty();
    }

    @Override
    public Optional<String> password() {
        return Optional.empty();
    }

    @Override
    public MailProtocol protocol() {
        return MailProtocol.smtp;
    }

    @Override
    public boolean skipServerCertificateValidation() {
        return false;
    }

    @Override
    public Optional<Email> sysAdminAddress() {
        return Optional.empty();
    }
    
}

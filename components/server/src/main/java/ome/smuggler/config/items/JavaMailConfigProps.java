package ome.smuggler.config.items;

import static util.config.StringConfigReaderFactory.intParser;
import static util.config.props.JPropAccessorFactory.makeBool;
import static util.config.props.JPropAccessorFactory.makeEnum;
import static util.config.props.JPropAccessorFactory.makeString;
import static util.config.props.JPropKey.key;

import java.time.Duration;

import ome.smuggler.core.types.MailProtocol;
import util.config.props.JPropAccessor;
import util.config.props.JPropKey;

/**
 * Accessors for Java Mail SMTP properties.
 * @see <a href="https://javamail.java.net/nonav/docs/api/com/sun/mail/smtp/package-summary.html">
 * Java Mail SMTP summary</a>
 */
public class JavaMailConfigProps {
    
    private static 
    JPropAccessor<Duration> makeDurationOfMillis(JPropKey k) {
        return new JPropAccessor<>(k, 
                intParser().andThen(Duration::ofMillis), 
                d -> String.valueOf(d.toMillis()));
    }
    // NB using intParser as all millis values in the java SMTP mail props page
    // are of type int.
    
    public static
    JPropAccessor<Boolean> debug() {
        return makeBool(key("mail.debug"));
    }
    
    public static
    JPropAccessor<MailProtocol> transportProtocol() {
        return makeEnum(MailProtocol.class, key("mail.transport.protocol"));
    }
    
    public static
    JPropAccessor<Duration> connectionTimeoutFor(MailProtocol p) {
        return makeDurationOfMillis(key("mail", p.name(), "connectiontimeout"));
    }
    
    public static
    JPropAccessor<Duration> readTimeoutFor(MailProtocol p) {
        return makeDurationOfMillis(key("mail", p.name(),"timeout"));
    }
    
    public static
    JPropAccessor<Duration> writeTimeoutFor(MailProtocol p) {
        return makeDurationOfMillis(key("mail", p.name(), "writetimeout"));
    }
    
    public static
    JPropAccessor<Boolean> authenticate(MailProtocol p) {
        return makeBool(key("mail", p.name(), "auth"));
    }
    
    public static
    JPropAccessor<Boolean> smtpEnableSsl() {
        return makeBool(key("mail.smtp.ssl.enable"));
    }
    
    public static
    JPropAccessor<Boolean> smtpsCheckServerIdentity() {
        return makeBool(key("mail.smtps.ssl.checkserveridentity"));
    }
    
    public static
    JPropAccessor<String> smtpsTrustedServers() {
        return makeString(key("mail.smtps.ssl.trust"));
    }
    
    public static
    JPropAccessor<Boolean> smtpEnableStartTls() {
        return makeBool(key("mail.smtp.starttls.enable"));
    }
    
    public static
    JPropAccessor<Boolean> smtpRequireStartTls() {
        return makeBool(key("mail.smtp.starttls.required"));
    }
    
}

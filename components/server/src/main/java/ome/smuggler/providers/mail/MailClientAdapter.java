package ome.smuggler.providers.mail;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.config.items.JavaMailConfigProps.authenticate;
import static ome.smuggler.config.items.JavaMailConfigProps.connectionTimeoutFor;
import static ome.smuggler.config.items.JavaMailConfigProps.readTimeoutFor;
import static ome.smuggler.config.items.JavaMailConfigProps.smtpsCheckServerIdentity;
import static ome.smuggler.config.items.JavaMailConfigProps.smtpsTrustedServers;
import static ome.smuggler.config.items.JavaMailConfigProps.writeTimeoutFor;
import static ome.smuggler.config.items.JavaMailConfigProps.transportProtocol;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import ome.smuggler.core.service.mail.MailClient;
import ome.smuggler.core.service.mail.MailClientException;
import ome.smuggler.core.types.MailConfigSource;
import ome.smuggler.core.types.MailProtocol;
import ome.smuggler.core.types.PlainTextMail;
import util.config.props.JProps;

public class MailClientAdapter implements MailClient {
    
    public static final Duration OpTimeout = Duration.ofMinutes(5); 
    
    private static void configureTransport(MailConfigSource config,
            JProps mailProps) {
        MailProtocol proto = config.protocol();
        mailProps.set(transportProtocol().with(proto));
        mailProps.set(connectionTimeoutFor(proto).with(OpTimeout));
        mailProps.set(readTimeoutFor(proto).with(OpTimeout));
        mailProps.set(writeTimeoutFor(proto).with(OpTimeout));
    }
    /* NOTE. Avoid sender hanging.
     * We need to avoid infinite timeouts (the default) as the HornetQ consumer
     * thread would be hanging forever and the queue would keep on growing. 
     * This is why we set the timeout properties.
     */
    
    private static void configureAuthentication(MailConfigSource config,
            String username, JavaMailSenderImpl mailSender, JProps mailProps) {
        mailSender.setUsername(username);
        config.password().ifPresent(mailSender::setPassword);
        mailProps.set(authenticate(config.protocol()).with(true));
    }
    
    private static void configureTls(MailConfigSource config, JProps mailProps) {
        mailProps.set(smtpsCheckServerIdentity().with(true));  // (1)
        if (config.skipServerCertificateValidation()) {        // (2)
            mailProps.set(smtpsTrustedServers()
                         .with(config.mailServer().getHost()));
        }
        
    }
    /* NOTES.
     * (1) This check needs to be on to prevent man-in-the-middle attacks, but
     * it's turned off by default in Java Mail for backward compatibility.
     * (2) Always returns false if the protocol is not SMTPS. Oh, BTW, this is
     * setting introduces a security vulnerability as already noted in config.
     */
    
    private static JavaMailSender build(MailConfigSource config) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        
        mailSender.setDefaultEncoding(StandardCharsets.UTF_8.name());
        mailSender.setHost(config.mailServer().getHost());
        mailSender.setPort(config.mailServer().getPort());
        
        JProps mailProps = new JProps(mailSender.getJavaMailProperties());
        configureTransport(config, mailProps);
        config.username().ifPresent(
                u -> configureAuthentication(config, u, mailSender, mailProps));
        if (MailProtocol.smtps.equals(config.protocol())) {
            configureTls(config, mailProps);
        }
        
        return mailSender;
    }
    
    private final MailConfigSource config;
    private final JavaMailSender service;
    
    public MailClientAdapter(MailConfigSource config) {
        requireNonNull(config, "config");
        
        this.config = config;
        this.service = build(config);
    }
    
    private MimeMessage message(PlainTextMail data) {
        requireNonNull(data, "data");
        
        MessageBuilder builder = new MessageBuilder(config, service);
        return builder.buildMimeMessage(data);
    }
    
    @Override
    public void send(PlainTextMail data) {
        try {
            service.send(message(data));
        } catch (MailException e) {
            throw new MailClientException(e);
        }
    }

    @Override
    public void stream(PlainTextMail data, OutputStream destination) {
        requireNonNull(destination, "destination");
        try {
            message(data).writeTo(destination);
        } catch (IOException | MessagingException e) {
            throw new MailClientException(e);
        }
    }

}

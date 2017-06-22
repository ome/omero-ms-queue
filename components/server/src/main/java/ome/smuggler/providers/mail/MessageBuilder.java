package ome.smuggler.providers.mail;

import static java.util.Objects.requireNonNull;

import java.nio.charset.StandardCharsets;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

import ome.smuggler.core.types.MailConfigSource;
import ome.smuggler.core.types.PlainTextMail;


public class MessageBuilder {

    private final MailConfigSource config;
    private final JavaMailSender mailClient;
    
    public MessageBuilder(MailConfigSource config, JavaMailSender mailClient) {
        requireNonNull(config, "config");
        requireNonNull(mailClient, "mailClient");
        
        this.config = config;
        this.mailClient = mailClient;
    }
    
    private MimeMailMessage mimeBuilder() {
        MimeMessageHelper helper = new MimeMessageHelper(
                                        mailClient.createMimeMessage(),
                                        StandardCharsets.UTF_8.name());
        return new MimeMailMessage(helper);
    }
    
    public MimeMessage buildMimeMessage(PlainTextMail data) {
        requireNonNull(data, "data");
        
        MimeMailMessage builder = mimeBuilder();
        builder.setFrom(config.fromAddress().get());
        builder.setTo(data.getRecipient().get());
        builder.setSubject(data.getSubject());
        builder.setText(data.getContent());
        
        return builder.getMimeMessage();
    }
    
}

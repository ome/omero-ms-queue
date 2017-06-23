package ome.smuggler.config.wiring.mail;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ome.smuggler.core.msg.ChannelSource;
import ome.smuggler.core.service.file.TaskFileStore;
import ome.smuggler.core.service.file.impl.TaskIdPathStore;
import ome.smuggler.core.service.mail.FailedMailHandler;
import ome.smuggler.core.service.mail.MailProcessor;
import ome.smuggler.core.service.mail.MailRequestor;
import ome.smuggler.core.service.mail.impl.MailEnv;
import ome.smuggler.core.service.mail.impl.MailFailureHandler;
import ome.smuggler.core.service.mail.impl.MailTrigger;
import ome.smuggler.core.service.mail.impl.Mailer;
import ome.smuggler.core.types.MailConfigSource;
import ome.smuggler.core.types.MailId;
import ome.smuggler.core.types.QueuedMail;
import ome.smuggler.providers.log.LogAdapter;
import ome.smuggler.providers.mail.MailClientAdapter;


/**
 * Spring bean wiring configuration for the mail service interfaces.
 */
@Configuration
public class MailServiceBeans {

    @Bean
    public TaskFileStore<MailId> failedMailStore(MailConfigSource config) {
        return new TaskIdPathStore<>(config.deadMailDir(), MailId::new);
    }
    
    @Bean
    public MailEnv mailEnv(MailConfigSource config, 
            ChannelSource<QueuedMail> mailSourceChannel,
            TaskFileStore<MailId> failedMailStore) {
        MailEnv env = new MailEnv(config, mailSourceChannel, 
                new MailClientAdapter(config), failedMailStore,
                new LogAdapter());
        
        env.ensureDirectories();
        return env;
    }
    
    @Bean 
    public MailRequestor mailRequestor(MailEnv env) {
        return new MailTrigger(env);
    }
    
    @Bean
    public MailProcessor mailProcessor(MailEnv env) {
        return new Mailer(env);
    }
    
    @Bean
    public FailedMailHandler failedMailHandler(MailEnv env) {
        return new MailFailureHandler(env);
    }
    
}

package ome.smuggler.core.service.mail.impl;

import static java.util.Objects.requireNonNull;
import static util.io.FileOps.ensureDirectory;

import ome.smuggler.core.msg.ChannelSource;
import ome.smuggler.core.service.file.TaskFileStore;
import ome.smuggler.core.service.log.LogService;
import ome.smuggler.core.service.mail.MailClient;
import ome.smuggler.core.types.MailConfigSource;
import ome.smuggler.core.types.MailId;
import ome.smuggler.core.types.QueuedMail;

/**
 * Provides access to mail configuration and queue.
 */
public class MailEnv {

    private final MailConfigSource config;
    private final ChannelSource<QueuedMail> queue;
    private final MailClient mailClient;
    private final TaskFileStore<MailId> failedMailStore;
    private final MailLogger log;
    
    public MailEnv(MailConfigSource config, ChannelSource<QueuedMail> queue, 
                   MailClient mailClient, TaskFileStore<MailId> failedMailStore, 
                   LogService logService) {
        requireNonNull(config, "config");
        requireNonNull(queue, "queue");
        requireNonNull(mailClient, "mailClient");
        requireNonNull(failedMailStore, "failedMailStore");
        requireNonNull(logService, "logService");
        
        this.config = config;
        this.queue = queue;
        this.mailClient = mailClient;
        this.failedMailStore = failedMailStore;
        this.log = new MailLogger(logService);
    }
    
    public MailConfigSource config() {
        return config;
    }
    
    public ChannelSource<QueuedMail> queue() {
        return queue;
    }
    
    public MailClient mailClient() {
        return mailClient;
    }
    
    public TaskFileStore<MailId> failedMailStore() {
        return failedMailStore;
    }
    
    public MailLogger log() {
        return log;
    }
    
    public void ensureDirectories() {
        ensureDirectory(config().deadMailDir());
    }
    
}

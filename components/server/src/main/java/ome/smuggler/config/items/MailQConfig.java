package ome.smuggler.config.items;

import org.hornetq.core.config.CoreQueueConfiguration;

/**
 * Holds the mail queue configuration.
 */
public class MailQConfig extends CoreQueueConfiguration {
    /* NB this has to be a Java Bean (i.e. getters/setters, no args ctor) to
     * be (de-)serialized painlessly by SnakeYaml.
     * CoreQueueConfiguration is only missing a no args ctor which we add below.  
     */ 

    private static final long serialVersionUID = 1L;

    public MailQConfig() { 
        super("", "", "", false);
    }
    
    public MailQConfig(String address, String name, String filterString,
            boolean durable) {
        super(address, name, filterString, durable);
    }

}

package ome.smuggler.config.items;

import org.apache.activemq.artemis.core.config.CoreQueueConfiguration;

/**
 * Holds the import queue configuration.
 */
public class ImportQConfig extends CoreQueueConfiguration {
    /* NB this has to be a Java Bean (i.e. getters/setters, no args ctor) to
     * be (de-)serialized painlessly by SnakeYaml.
     * CoreQueueConfiguration is only missing a no args ctor which we add below.  
     */ 

    private static final long serialVersionUID = 1L;

    public ImportQConfig() { 
        this("", "", "", false);
    }
    
    public ImportQConfig(String address, String name, String filterString,
            boolean durable) {
        setAddress(address);
        setName(name);
        setFilterString(filterString);
        setDurable(durable);
    }

}

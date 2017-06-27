package ome.smuggler.config.items;

import org.apache.activemq.artemis.core.config.CoreQueueConfiguration;

/**
 * Holds the OMERO session queue configuration.
 */
public class OmeroSessionQConfig extends CoreQueueConfiguration {
    /* NB this has to be a Java Bean (i.e. getters/setters, no args ctor) to
     * be (de-)serialized painlessly by SnakeYaml.
     * CoreQueueConfiguration is only missing a no args ctor which we add below.
     */

    private static final long serialVersionUID = 1L;

    public OmeroSessionQConfig() {
        this("", "", "", false);
    }

    public OmeroSessionQConfig(String address, String name, String filterString,
                               boolean durable) {
        setAddress(address);
        setName(name);
        setFilterString(filterString);
        setDurable(durable);
    }

}

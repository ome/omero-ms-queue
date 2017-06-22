package ome.smuggler.config.providers;

import org.springframework.stereotype.Component;

import ome.smuggler.config.data.DefaultOmeroSessionQConfig;


/**
 * The OMERO session queue configuration as required by HornetQ.
 * This configuration is hard-coded as the queue is only used internally by the
 * OMERO session service.
 */
@Component
public class OmeroSessionQConfigProvider extends DefaultOmeroSessionQConfig {

}

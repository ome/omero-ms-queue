package ome.smuggler.config.providers;

import org.springframework.stereotype.Component;

import ome.smuggler.config.data.DefaultImportQConfig;


/**
 * The import queue configuration as required by HornetQ.
 * This configuration is hard-coded as the queue is only used internally by the
 * import server.
 */
@Component
public class ImportQConfigProvider extends DefaultImportQConfig {

}

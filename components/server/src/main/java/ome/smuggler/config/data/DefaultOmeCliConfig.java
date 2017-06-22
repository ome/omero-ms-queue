package ome.smuggler.config.data;

import java.util.stream.Stream;

import ome.smuggler.config.items.OmeCliConfig;
import util.config.ConfigProvider;


/**
 * Hard-coded configuration for launching the OMERO CLI commands.
 */
public class DefaultOmeCliConfig 
    implements ConfigProvider<OmeCliConfig> {

    @Override
    public Stream<OmeCliConfig> readConfig() throws Exception {
        OmeCliConfig cfg = new OmeCliConfig();
        return Stream.of(cfg);
    }

}

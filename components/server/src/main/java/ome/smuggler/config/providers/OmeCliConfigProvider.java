package ome.smuggler.config.providers;

import ome.smuggler.config.data.DefaultOmeCliConfig;
import ome.smuggler.config.items.OmeCliConfig;
import util.spring.io.ResourceReader;

import java.util.stream.Stream;

import org.springframework.stereotype.Component;


/**
 * The data needed to configure the OMERO CLI commands.
 */
@Component
public class OmeCliConfigProvider extends PriorityConfigProvider<OmeCliConfig> {

    public static final String FileName = "ome-cli.yml";

    @Override
    protected ResourceReader<OmeCliConfig> getConverter() {
        return new YmlResourceReader<>(OmeCliConfig.class);
    }

    @Override 
    public Stream<OmeCliConfig> getFallback() {
        return new DefaultOmeCliConfig().defaultReadConfig();
    }

    @Override
    public String getConfigFileName() {
        return FileName;
    }

}

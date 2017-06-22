package ome.smuggler.config.providers;

import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import util.spring.io.ResourceReader;
import ome.smuggler.config.data.UndertowYmlFile;
import ome.smuggler.config.items.UndertowConfig;


/**
 * Reads Undertow configuration from a YAML file, falling back to hard-coded
 * configuration if no file is available.
 * This provider will first try to read the file from the current directory; 
 * failing that, it will try to find the file in the class-path, falling back
 * to hard-code config if not found.
 */
@Component
public class UndertowConfigProvider 
    extends PriorityConfigProvider<UndertowConfig> {

    public static final String FileName = "undertow.yml";
    
    @Override
    protected ResourceReader<UndertowConfig> getConverter() {
        return new YmlResourceReader<>(UndertowConfig.class);
    }
    
    @Override 
    public Stream<UndertowConfig> getFallback() {
        return new UndertowYmlFile().readConfig();
    }

    @Override
    public String getConfigFileName() {
        return FileName;
    }
    
}

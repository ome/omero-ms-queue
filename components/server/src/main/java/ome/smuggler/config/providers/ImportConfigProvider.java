package ome.smuggler.config.providers;

import java.util.stream.Stream;

import ome.smuggler.config.data.ImportYmlFile;
import ome.smuggler.config.items.ImportConfig;

import org.springframework.stereotype.Component;

import util.spring.io.ResourceReader;

/**
 * Reads import configuration from a YAML file, falling back to hard-coded
 * configuration if no file is available.
 * This provider will first try to read the file from the current directory; 
 * failing that, it will try to find the file in the class-path, falling back
 * to hard-code config if not found.
 */
@Component
public class ImportConfigProvider 
    extends PriorityConfigProvider<ImportConfig> {

    public static final String FileName = "import.yml";
    
    @Override
    protected ResourceReader<ImportConfig> getConverter() {
        return new YmlResourceReader<>(ImportConfig.class);
    }
    
    @Override 
    public Stream<ImportConfig> getFallback() {
        return new ImportYmlFile().readConfig();
    }

    @Override
    public String getConfigFileName() {
        return FileName;
    }

}

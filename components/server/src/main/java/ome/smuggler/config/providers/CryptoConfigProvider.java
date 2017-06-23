package ome.smuggler.config.providers;

import java.util.stream.Stream;

import org.springframework.stereotype.Component;
import util.spring.io.ResourceReader;

import ome.smuggler.config.data.CryptoYmlFile;
import ome.smuggler.config.items.CryptoConfig;


/**
 * Reads crypto configuration from a YAML file, falling back to hard-coded
 * configuration if no file is available.
 * This provider will first try to read the file from the current directory;
 * failing that, it will try to find the file in the class-path, falling back
 * to hard-code config if not found.
 */
@Component
public class CryptoConfigProvider
        extends PriorityConfigProvider<CryptoConfig> {

    public static final String FileName = "crypto.yml";

    @Override
    protected ResourceReader<CryptoConfig> getConverter() {
        return new YmlResourceReader<>(CryptoConfig.class);
    }

    @Override
    public Stream<CryptoConfig> getFallback() {
        return new CryptoYmlFile().readConfig();
    }

    @Override
    public String getConfigFileName() {
        return FileName;
    }

}

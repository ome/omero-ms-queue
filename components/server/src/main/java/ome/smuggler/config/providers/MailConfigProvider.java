package ome.smuggler.config.providers;

import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import util.spring.io.ResourceReader;
import ome.smuggler.config.data.MailYmlFile;
import ome.smuggler.config.items.MailConfig;


/**
 * Reads mail server configuration from a YAML file, falling back to hard-coded
 * configuration if no file is available.
 * This provider will first try to read the file from the current directory; 
 * failing that, it will try to find the file in the class-path, falling back
 * to hard-code config if not found.
 */
@Component
public class MailConfigProvider extends PriorityConfigProvider<MailConfig> {

    public static final String FileName = "mail.yml";
    
    @Override
    protected ResourceReader<MailConfig> getConverter() {
        return new YmlResourceReader<>(MailConfig.class);
    }
    
    @Override 
    public Stream<MailConfig> getFallback() {
        return new MailYmlFile().readConfig();
    }

    @Override
    public String getConfigFileName() {
        return FileName;
    }
    
}

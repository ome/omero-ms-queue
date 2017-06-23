package ome.smuggler.run;

import java.util.List;

import util.config.YamlConverter;
import ome.smuggler.config.data.UndertowYmlFile;
import ome.smuggler.config.items.UndertowConfig;

/**
 * Run this class redirecting {@code stdout} to {@code config/undertow.yml} to 
 * generate the file. This way we can keep all config data in Java and avoid 
 * any deserialization issue.
 * <pre>
 * java -jar build/libs/ome-smuggler-*.jar ome.smuggler.run.UndertowYmlGen \
 * {@literal >} src/main/resources/config/undertow.yml
 *</pre>
 */
public class UndertowYmlGen implements RunnableApp {

    /**
     * Dumps YAML config to {@code stdout}.
     */
    @Override
    public void run(List<String> appArgs) {
        UndertowConfig fileContents = new UndertowYmlFile()
                                     .readConfig()
                                     .findFirst()
                                     .get();
        String yaml = new YamlConverter<UndertowConfig>().toYaml(fileContents);
        System.out.print(yaml); 
    }

}

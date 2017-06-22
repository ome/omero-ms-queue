package ome.smuggler.run;

import java.util.List;

import ome.smuggler.config.data.ImportYmlFile;
import ome.smuggler.config.items.ImportConfig;
import util.config.YamlConverter;

/**
 * Run this class redirecting {@code stdout} to {@code config/import.yml} to 
 * generate the file. This way we can keep all config data in Java and avoid 
 * any deserialization issue.
 * <pre>
 * java -jar build/libs/ome-smuggler-*.jar ome.smuggler.run.ImportYmlGen \
 * {@literal >} src/main/resources/config/import.yml
 *</pre>
 */
public class ImportYmlGen implements RunnableApp {

    /**
     * Dumps YAML config to {@code stdout}.
     */
    @Override
    public void run(List<String> appArgs) {
        ImportConfig fileContents = new ImportYmlFile()
                                     .readConfig()
                                     .findFirst()
                                     .get();
        String yaml = new YamlConverter<ImportConfig>().toYaml(fileContents);
        System.out.print(yaml); 
    }

}

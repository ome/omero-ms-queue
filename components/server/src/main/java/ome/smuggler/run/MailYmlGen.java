package ome.smuggler.run;

import java.util.List;

import ome.smuggler.config.data.MailYmlFile;
import ome.smuggler.config.items.MailConfig;
import util.serialization.yaml.YamlConverter;

/**
 * Run this class redirecting {@code stdout} to {@code config/mail.yml} to 
 * generate the file. This way we can keep all config data in Java and avoid 
 * any deserialization issue.
 * <pre>
 * java -jar build/libs/ome-smuggler-*.jar ome.smuggler.run.MailYmlGen \
 * {@literal >} src/main/resources/config/mail.yml
 *</pre>
 */
public class MailYmlGen implements RunnableApp {

    /**
     * Dumps YAML config to {@code stdout}.
     */
    @Override
    public void run(List<String> appArgs) {
        MailConfig fileContents = new MailYmlFile()
                                     .readConfig()
                                     .findFirst()
                                     .get();
        String yaml = new YamlConverter<MailConfig>().toYaml(fileContents);
        System.out.print(yaml); 
    }

}

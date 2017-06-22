package ome.smuggler.run;

import ome.smuggler.config.data.CryptoYmlFile;
import ome.smuggler.config.items.CryptoConfig;
import util.config.YamlConverter;

import java.util.List;

/**
 * Run this class redirecting {@code stdout} to {@code config/crypto.yml} to
 * generate the file. This way we can keep all config data in Java and avoid
 * any deserialization issue.
 * <pre>
 * java -jar build/libs/ome-smuggler-*.jar ome.smuggler.run.CryptoYmlGen \
 * {@literal >} src/main/resources/config/crypto.yml
 *</pre>
 */
public class CryptoYmlGen implements RunnableApp {

    /**
     * Dumps YAML config to {@code stdout}.
     */
    @Override
    public void run(List<String> appArgs) {
        CryptoConfig fileContents = new CryptoYmlFile().first();
        String yaml = new YamlConverter<CryptoConfig>().toYaml(fileContents);
        System.out.print(yaml);
    }

}

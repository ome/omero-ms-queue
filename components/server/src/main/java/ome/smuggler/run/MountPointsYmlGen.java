package ome.smuggler.run;

import java.util.List;

import ome.smuggler.config.data.MountPointsYmlFile;
import ome.smuggler.config.items.MountPointsConfig;
import util.config.YamlConverter;


/**
 * Run this class redirecting {@code stdout} to {@code config/mount-points.yml}
 * to generate the file. This way we can keep all config data in Java and avoid
 * any deserialization issue.
 * <pre>
 * java -jar build/libs/ome-smuggler-*.jar ome.smuggler.run.MountPointsYmlGen \
 * {@literal >} src/main/resources/config/mount-points.yml
 *</pre>
 */
public class MountPointsYmlGen implements RunnableApp {

    /**
     * Dumps YAML config to {@code stdout}.
     */
    @Override
    public void run(List<String> appArgs) {
        MountPointsConfig fileContents = new MountPointsYmlFile().first();
        String yaml = new YamlConverter<MountPointsConfig>()
                     .toYaml(fileContents);
        System.out.print(yaml);
    }

}

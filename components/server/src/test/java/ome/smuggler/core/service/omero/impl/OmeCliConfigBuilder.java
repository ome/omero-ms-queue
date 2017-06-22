package ome.smuggler.core.service.omero.impl;

import ome.smuggler.config.data.DefaultOmeCliConfig;
import ome.smuggler.config.items.OmeCliConfig;
import ome.smuggler.core.types.OmeCliConfigReader;
import ome.smuggler.core.types.OmeCliConfigSource;

public class OmeCliConfigBuilder {

    public static OmeCliConfigSource config() {
        OmeCliConfig cfg = new DefaultOmeCliConfig()
                .defaultReadConfig()
                .findFirst()
                .get();
        cfg.setOmeCliJarPath("any-name-will-do");  // (*)

        return new OmeCliConfigReader(cfg);
    }
    /* (*) Avoid bombing out on ome-cli jar.
     * If no jar path is configured, the OmeCliConfigReader will try locating an
     * ome-cli jar file in the same directory as Smuggler's jar. This is fine
     * when running the app, but as we test there will be no jar files as the
     * tests are run straight from the compiled classes in the build directory.
     * So we explicitly set a value for the ome-cli jar file which results in
     * the OmeCliConfigReader just returning the value as a path.
     */
}

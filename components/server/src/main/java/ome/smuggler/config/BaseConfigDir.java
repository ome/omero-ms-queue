package ome.smuggler.config;

import util.config.BaseDir;

/**
 * The base directory where to look for configuration files.
 * This path can be specified in the {@link #SysPropKey system properties} at 
 * start up or as an {@link #EnvVarName environment variable}. System properties
 * take precedence over environment variables. If the path is not found in
 * either places, it will default to the current working directory of the
 * Smuggler's process.
 * @see ome.smuggler.config.providers.PriorityConfigProvider
 */
public class BaseConfigDir extends BaseDir {

    public static final String SysPropKey = "ome.smuggler.ConfigDir";
    public static final String EnvVarName = "SMUGGLER_CONFIGDIR";

    /**
     * Creates a new instance to read the directory value from the system
     * properties or environment, defaulting to the current working directory
     * if no value is found.
     */
    public BaseConfigDir() {
        super(SysPropKey, EnvVarName);
    }

}

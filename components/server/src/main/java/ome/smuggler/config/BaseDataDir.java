package ome.smuggler.config;


/**
 * The base directory where we keep all the import, mail, and HornetQ data.
 * This path can be specified in the {@link #SysPropKey system properties} at
 * start up or as an {@link #EnvVarName environment variable}. System properties
 * take precedence over environment variables. If the path is not found in
 * either places, it will default to the current working directory of the
 * Smuggler's process.
 */
public class BaseDataDir extends BaseDir {

    public static final String SysPropKey = "ome.smuggler.DataDir";
    public static final String EnvVarName = "SMUGGLER_DATADIR";

    /**
     * Creates a new instance to read the directory value from the system
     * properties or environment, defaulting to the current working directory
     * if no value is found.
     */
    public BaseDataDir() {
        super(SysPropKey, EnvVarName);
    }

}

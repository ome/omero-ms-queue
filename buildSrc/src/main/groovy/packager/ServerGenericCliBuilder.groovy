package packager

/**
 * Builds the arguments to pass to the java command for starting the server.
 * Use for the scripts in the "generic" distribution.
 */
class ServerGenericCliBuilder {

    static final String configDirKey = 'ome.smuggler.ConfigDir'
    static final String dataDirKey = 'ome.smuggler.DataDir'
    static final String loggingPathKey = 'logging.path'

    static String sysPropArg(String name, String value) {
        String.format('-D%s="%s"', name, value)
    }

    static String jarArg(String path) {
        String.format('-jar "%s"', path)
    }

    static String configDirArg(String path) {
        sysPropArg(configDirKey, path)
    }

    static String dataDirArg(String path) {
        sysPropArg(dataDirKey, path)
    }

    static String logDirArg(String path) {
        sysPropArg(loggingPathKey, path)
    }

    static String buildWindowsArgs(BuildDir dirs, String serverJarName) {
        new ServerGenericCliBuilder('%APP_HOME%', '\\', dirs, serverJarName)
                .build()
    }

    static String buildUnixArgs(BuildDir dirs, String serverJarName) {
        new ServerGenericCliBuilder('$APP_HOME', '/', dirs, serverJarName)
                .build()
    }

    String appHomeVar
    String separator
    BuildDir dirs
    String serverJarName

    ServerGenericCliBuilder(String appHomeVar, String separator, BuildDir dirs,
                            String serverJarName) {
        this.appHomeVar = appHomeVar
        this.separator = separator
        this.dirs = dirs
        this.serverJarName = serverJarName
    }

    String path(p) {
        String.format('%s%s%s', appHomeVar, separator, p)
    }

    String jar() {
        jarArg(path(dirs.staging.generic.libDir.name) + separator + serverJarName)
    }

    String config() {
        configDirArg(path(dirs.staging.generic.configDir.name))
    }

    String data() {
        dataDirArg(path(dirs.staging.generic.dataDir.name))
    }

    String log() {
        logDirArg(path(dirs.staging.generic.logDir.name))
    }

    String build() {
        [config(), data(), log(), jar()].join(' ')
    }

}

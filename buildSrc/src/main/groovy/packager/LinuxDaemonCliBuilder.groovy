package packager

import static ServerGenericCliBuilder.logDirArg

/**
 * Builds the arguments to pass to the java command for starting the Spring Boot
 * Linux daemon.
 * Use for the scripts in the "linux-daemon" distribution.
 */
class LinuxDaemonCliBuilder {

    static final String configDirVar = 'SMUGGLER_CONFIGDIR'
    static final String dataDirVar = 'SMUGGLER_DATADIR'

    static String envVar(String name, String value) {
        String.format('%s="%s"', name, value)
    }

    static String configDirVar(String value) {
        envVar(configDirVar, value)
    }

    static String dataDirVar(String value) {
        envVar(dataDirVar, value)
    }


    ReleaseInfo info

    LinuxDaemonCliBuilder(ReleaseInfo info) {
        this.info = info
    }

    String suggestedConfig() {
        configDirVar('/etc/opt/' + info.baseName + '.d')
    }

    String config() {
        configDirVar('config')
    }

    String data() {
        dataDirVar('data')
    }

    String suggestedData() {
        dataDirVar('/var/opt/' + info.baseName)
    }

    String log() {
        logDirArg('log')
    }

    String suggestedLog() {
        logDirArg('/var/log/' + info.baseName)
    }

}

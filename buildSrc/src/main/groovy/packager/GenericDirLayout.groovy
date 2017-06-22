package packager

import static util.Filez.*

/**
 * The directory layout used for the "generic" and "winsvc" distributions.
 */
class GenericDirLayout {

    File baseDir, binDir, configDir, dataDir, libDir, logDir

    GenericDirLayout(File baseDir) {
        this.baseDir = baseDir
        binDir = toFile(baseDir, 'bin')
        configDir = toFile(baseDir, 'config')
        dataDir = toFile(baseDir, 'data')
        libDir = toFile(baseDir, 'lib')
        logDir = toFile(baseDir, 'log')
    }

    def subdirs() {
        [binDir, configDir, dataDir, libDir, logDir]
    }

}

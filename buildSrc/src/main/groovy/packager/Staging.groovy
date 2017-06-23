package packager

import static util.Filez.*

/**
 * Defines the packager's staging directory layout.
 * We use this directory to collect the various bits and pieces that will then
 * be used to create the release bundles.
 */
class Staging {

    File buildDir, baseDir, linuxDaemonDir
    GenericDirLayout generic, winsvc

    Staging(File buildDir) {
        this.buildDir = buildDir
        baseDir = toFile(buildDir, 'staging')
        generic = new GenericDirLayout(toFile(baseDir, 'generic'))
        winsvc = new GenericDirLayout(toFile(baseDir, 'winsvc'))
        linuxDaemonDir = toFile(baseDir, 'linux-daemon')
    }

    def subdirs() {
        generic.subdirs() + winsvc.subdirs() + [linuxDaemonDir]
    }

}

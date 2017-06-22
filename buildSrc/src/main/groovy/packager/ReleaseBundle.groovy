package packager

import org.gradle.api.tasks.bundling.AbstractArchiveTask

/**
 * Configures archive tasks to collect files from the packager's staging area,
 * depending on release type, e.g. generic release, windows service, etc.
 */
class ReleaseBundle {

    BuildDir dirs
    ReleaseInfo info

    ReleaseBundle(BuildDir dirs, ReleaseInfo info) {
        this.dirs = dirs
        this.info = info
    }

    def configureBase(AbstractArchiveTask task) {
        task.into info.baseName
        task.include '**/*'
        task.includeEmptyDirs = true
        task.destinationDir = dirs.distDir
        task.baseName = info.baseName
        task.version = info.version
        task.classifier = info.classifier
    }

    def configureGeneric(AbstractArchiveTask task) {
        configureBase(task)
        task.from dirs.staging.generic.baseDir
    }

    def configureWinService(AbstractArchiveTask task) {
        configureBase(task)
        task.from dirs.staging.winsvc.baseDir
        task.baseName = info.baseName + '-winsvc'
    }

    def configureLinuxDaemon(AbstractArchiveTask task) {
        configureBase(task)
        task.from dirs.staging.linuxDaemonDir
        task.baseName = info.baseName + '-linux-daemon'
    }

}

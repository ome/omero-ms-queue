package util

import java.nio.file.Paths
import org.gradle.api.tasks.bundling.Jar

/**
 * Utility methods to operate on file paths as Gradle likes File better than
 * Path.
 */
class Filez {

    /**
     * Combines the base path with the rest of the path elements.
     * Examples:
     * <pre>
     *     File base = new File('mydir')
     *     toFile(base)                       ~~== mydir
     *     toFile(base, 'some')               ~~== mydir/some
     *     toFile(base, 'some/other')         ~~== mydir/some/other
     *     toFile(base, 'some/other', 'path') ~~== mydir/some/other/path
     * </pre>
     * @param base the base path.
     * @param ps the rest of the path elements.
     * @return the combined path as a file.
     */
    static File toFile(File base, String...ps) {
        Paths.get(base.absolutePath, ps).toFile()
    }

    /**
     * Converts the Jar task's archive path into a file.
     * @param task the task.
     * @return the file.
     */
    static File toFile(Jar task) {
        toFile(task.archivePath)
    }

    /**
     * Extracts the file name without extension from the given file.
     * @param pathname the file.
     * @return the file name without extension.
     */
    static String nameWithoutExtension(File pathname) {
        pathname.name.replaceFirst('[.][^.]+$', '')
    }

}
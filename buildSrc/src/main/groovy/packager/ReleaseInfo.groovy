package packager

/**
 * Stores release info such as version and name.
 */
class ReleaseInfo {

    ReleaseInfo(Map<String, String> props) {
        baseName = props['baseName']
        version = props['version']
        classifier = props['classifier']
    }

    /**
     * Release bundle base name, e.g. "ome-smuggler".
     */
    String baseName

    /**
     * Release bundle version, e.g. "2.1.0".
     */
    String version

    /**
     * Release bundle classifier, e.g. "beta".
     */
    String classifier

}

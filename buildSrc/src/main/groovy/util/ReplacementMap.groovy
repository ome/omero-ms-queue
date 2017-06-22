package util

/**
 * A map with the ability to replace key occurrences with corresponding values
 * in a given string.
 */
class ReplacementMap<K, V> extends HashMap<K, V> {

    /**
     * For each key {@code k} in this map, replaces any occurrence of {@code k}
     * in the input string with the value associated to {@code k} in this map.
     * @param x the input string to process.
     * @return a new string with the replaced values.
     */
    String replaceAll(String x) {
        inject(x, { r, e ->
            r.replace(e.key.toString(), e.value.toString())
        })
    }

}

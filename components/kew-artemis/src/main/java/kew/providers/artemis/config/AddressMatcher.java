package kew.providers.artemis.config;

import static java.util.Objects.requireNonNull;
import static util.string.Strings.requireString;

/**
 * Builds strings to use for matching Artemis addresses.
 */
public class AddressMatcher {

    private final String anyWordsWildcard;
    // private final String singleWordWildcard;
    // private final String wordDelimiter;
    // NB not used at the moment, but might use going forward...

    /**
     * Creates a new instance with the specified Artemis word matcher.
     * This will have to be the same as the one you specified in the
     * Artemis wildcard configuration section.
     * @param anyWordsWildcard the "any words" wildcard.
     * @throws IllegalArgumentException if the argument is {@code null} or
     * empty.
     */
    public AddressMatcher(String anyWordsWildcard) {
        requireString(anyWordsWildcard, "anyWordsWildcard");
        this.anyWordsWildcard = anyWordsWildcard;
    }

    /**
     * Matches any address.
     * @return the matcher.
     */
    public String anyAddress() {
        return anyAddressStartingWith("");
    }

    /**
     * Matches any address starting with the given prefix.
     * @param prefix the address prefix.
     * @return the matcher.
     * @throws IllegalArgumentException if the prefix is {@code null} or empty.
     */
    public String anyAddressStartingWith(String prefix) {
        requireNonNull(prefix, "prefix");
        return prefix + anyWordsWildcard;
    }

}

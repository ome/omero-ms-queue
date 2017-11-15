package kew.providers.artemis.config;

/**
 * Utility methods to match addresses using Artemis default wildcard syntax.
 */
public class AddressMatchers {

    /**
     * Artemis default "any words" wildcard.
     */
    public static final String DefaultAnyWordsWildcard = "#";
    // public static final String DefaultSingleWordWildcard = "*";
    // public static final String DefaultWordDelimiter = ".";
    // NB not used at the moment, but might use going forward...

    private static final AddressMatcher defaultMatcher =
            new AddressMatcher(DefaultAnyWordsWildcard);

    /**
     * Matches any address.
     * @return the matcher.
     */
    public static String anyAddress() {
        return defaultMatcher.anyAddress();
    }

    /**
     * Matches any address starting with the given prefix.
     * @param prefix the address prefix.
     * @return the matcher.
     * @throws IllegalArgumentException if the prefix is {@code null} or empty.
     */
    public static String anyAddressStartingWith(String prefix) {
        return defaultMatcher.anyAddressStartingWith(prefix);
    }

}

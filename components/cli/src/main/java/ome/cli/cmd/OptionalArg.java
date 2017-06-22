package ome.cli.cmd;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An optional argument to a {@link Command}.
 * Optional arguments are passed as JVM properties on the command line.
 */
class OptionalArg<T> implements Supplier<Optional<T>> {

    private final String key;
    private final Function<String, T> parser;

    OptionalArg(String key, Function<String, T> parser) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("missing key");
        }
        requireNonNull(parser, "parser");

        this.key = key;
        this.parser = parser;
    }

    @Override
    public Optional<T> get() {
        return Optional.ofNullable(System.getProperty(key)).map(parser);
    }

}

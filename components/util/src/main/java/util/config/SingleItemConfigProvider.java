package util.config;

import static java.util.Objects.requireNonNull;
import static util.sequence.Streams.asStream;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;


/**
 * Looks up a configuration item using a supplier.
 */
public class SingleItemConfigProvider<T> 
    implements ConfigProvider<T>, Supplier<Optional<T>> {

    private final Supplier<T> lookup;
    
    /**
     * Creates a new instance to read a configuration item given a supplier.
     * @param lookup supplies the configuration item, can return {@code null}
     * if the item is not found.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public SingleItemConfigProvider(Supplier<T> lookup) {
        requireNonNull(lookup, "lookup");
        this.lookup = lookup;
    }
    
    /**
     * Uses the underlying supplier to look up the configuration item.
     * If the supplier returns {@code null} (e.g. item not found), then the 
     * returned stream will be empty; otherwise it will contain whichever
     * object the supplier returned.
     */
    @Override
    public Stream<T> readConfig() throws Exception {
        return asStream(get());
    }

    /**
     * Uses the underlying supplier to look up the configuration item.
     * If the supplier returns {@code null} (e.g. item not found), then the 
     * returned optional will be empty; otherwise it will contain whichever
     * object the supplier returned.
     */
    @Override
    public Optional<T> get() {
        return Optional.ofNullable(lookup.get());
    }
    
}

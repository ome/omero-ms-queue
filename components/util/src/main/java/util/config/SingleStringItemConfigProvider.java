package util.config;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Specializes {@link SingleItemConfigProvider} to string items so that if the
 * looked up string value is {@code null} or empty, the returned stream (or
 * optional) will be empty. 
 */
public class SingleStringItemConfigProvider 
    extends SingleItemConfigProvider<String> {

    /**
     * Creates a new instance to read a configuration item given a supplier.
     * @param lookup supplies the configuration item, can return {@code null}
     * or empty if the item is not found.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public SingleStringItemConfigProvider(Supplier<String> lookup) {
        super(lookup);
    }

    /**
     * Uses the underlying supplier to look up the configuration item.
     * If the supplier returns {@code null} (e.g. item not found) or empty, then
     * the returned optional will be empty; otherwise it will contain whichever
     * object the supplier returned.
     */
    @Override
    public Optional<String> get() {
        return super.get().filter(x -> x != "");
    }
    
}

package util.config;

import static java.util.Objects.requireNonNull;
import static util.error.Exceptions.unchecked;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A {@link ConfigProvider configuration provider} that reads items of type
 * {@code T} from a source provider and transform them into objects of type 
 * {@code M}.
 */
public abstract class ConfigReader<T, M> implements ConfigProvider<M> {

    /**
     * Creates a new configuration provider out of a source configuration
     * provider so that its source items of type {@code T} are transformed
     * into objects of type {@code M}.
     * @param <T> source item type.
     * @param <M> mapped item type.
     * @param configSource reads items of type {@code T} from configuration.
     * @param itemMapper the transformation to apply to each item read from 
     * {@code configSource} to produce objects of type {@code M}.
     * @return a new configuration provider to transform the source items.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static <T, M> ConfigProvider<M> newReader(
            ConfigProvider<T> configSource,
            Function<T, M> itemMapper) {
        requireNonNull(itemMapper, "itemMapper");
        
        return new ConfigReader<T, M>(configSource) {
            @Override
            protected M mapItem(T configEntry) {
                return itemMapper.apply(configEntry);
            }
        };
    }
    
    protected final ConfigProvider<T> configSource;
    
    protected ConfigReader(ConfigProvider<T> configSource) {
        requireNonNull(configSource);
        this.configSource = configSource;
    }
    
    /**
     * A transformation to apply to each item read from our configuration 
     *{@link #configSource  provider}.
     * @param configEntry the configuration item to transform.
     * @return the transformed item.
     * @throws Exception if a transformation error occurs, e.g. parsing a string
     * into an integer.
     */
    protected abstract M mapItem(T configEntry) throws Exception;

    @Override
    public Stream<M> readConfig() throws Exception {
        return configSource.readConfig()
                           .map(unchecked(this::mapItem));
    }

}

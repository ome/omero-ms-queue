package ome.smuggler.config.providers;

import static java.util.Objects.requireNonNull;

import java.io.InputStream;
import java.util.stream.Stream;

import util.config.YamlConverter;
import util.spring.io.ResourceReader;

/**
 * A resource reader to read a configuration item of type {@code T} from its
 * YAML representation.
 */
public class YmlResourceReader<T> implements ResourceReader<T> {

    private final Class<T> configItemType;
    
    /**
     * Creates a new instance.
     * @param configItemType the type of the configuration item to read.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public YmlResourceReader(Class<T> configItemType) {
        requireNonNull(configItemType, "configItemType");
        this.configItemType = configItemType;
    }
    
    @Override
    public Stream<T> convert(InputStream data) throws Exception {
        YamlConverter<T> converter = new YamlConverter<>(); 
        T configItem = converter.fromYaml(data, configItemType);
        
        return Stream.of(configItem);
    }

}

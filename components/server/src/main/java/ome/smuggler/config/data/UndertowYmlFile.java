package ome.smuggler.config.data;

import java.util.stream.Stream;

import util.config.ConfigProvider;
import ome.smuggler.config.items.UndertowConfig;

/**
 * The data that goes into 'config/undertow.yml'.
 */
public class UndertowYmlFile implements ConfigProvider<UndertowConfig> {

    @Override
    public Stream<UndertowConfig> readConfig() {
        UndertowConfig cfg = new UndertowConfig();
        cfg.setPort(8000);
        
        return Stream.of(cfg);
    }

}

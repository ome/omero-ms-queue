package ome.smuggler.config.data;

import java.util.stream.Stream;

import ome.smuggler.config.items.ImportQConfig;

import util.config.ConfigProvider;

/**
 * Hard-coded import queue configuration.
 */
public class DefaultImportQConfig implements ConfigProvider<ImportQConfig> {

    @Override
    public Stream<ImportQConfig> readConfig() throws Exception {
        String address = "ome/import";
        String name = address;
        String filter = "";
        boolean durable = true;
        
        return Stream.of(new ImportQConfig(address, name, filter, durable));
    }

}

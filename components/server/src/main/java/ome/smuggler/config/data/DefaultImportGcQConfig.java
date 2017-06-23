package ome.smuggler.config.data;

import java.util.stream.Stream;

import ome.smuggler.config.items.ImportGcQConfig;
import util.config.ConfigProvider;

/**
 * Hard-coded configuration for the import garbage collection queue.
 */
public class DefaultImportGcQConfig implements ConfigProvider<ImportGcQConfig> {

    @Override
    public Stream<ImportGcQConfig> readConfig() throws Exception {
        String address = "ome/import/gc";
        String name = address;
        String filter = "";
        boolean durable = true;
        
        return Stream.of(new ImportGcQConfig(address, name, filter, durable));
    }

}

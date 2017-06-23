package ome.smuggler.config.data;

import java.util.stream.Stream;

import ome.smuggler.config.items.OmeroSessionQConfig;
import util.config.ConfigProvider;

/**
 * Hard-coded OMERO session queue configuration.
 */
public class DefaultOmeroSessionQConfig
        implements ConfigProvider<OmeroSessionQConfig> {

    @Override
    public Stream<OmeroSessionQConfig> readConfig() throws Exception {
        String address = "ome/session";
        String name = address;
        String filter = "";
        boolean durable = true;

        return Stream.of(
                new OmeroSessionQConfig(address, name, filter, durable));
    }

}

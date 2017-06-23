package ome.smuggler.config.data;

import java.util.stream.Stream;

import ome.smuggler.config.items.MailQConfig;

import util.config.ConfigProvider;

/**
 * Hard-coded mail queue configuration.
 */
public class DefaultMailQConfig implements ConfigProvider<MailQConfig> {

    @Override
    public Stream<MailQConfig> readConfig() throws Exception {
        String address = "ome/mail";
        String name = address;
        String filter = "";
        boolean durable = true;
        
        return Stream.of(new MailQConfig(address, name, filter, durable));
    }

}

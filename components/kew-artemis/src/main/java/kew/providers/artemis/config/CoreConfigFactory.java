package kew.providers.artemis.config;

import static java.util.Objects.requireNonNull;
import static util.error.Exceptions.unchecked;

import java.net.URI;

import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.config.FileDeploymentManager;
import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl;
import org.apache.activemq.artemis.core.config.impl.FileConfiguration;

import util.object.Builder;

/**
 * Methods to read and build Artemis core configuration.
 */
public class CoreConfigFactory {

    /**
     * @return the default classpath location of the Artemis broker config
     * file.
     */
    public static URI defaultBrokerXmlConfigLocation() {
        return URI.create("broker.xml");
    }

    /**
     * Reads the Artemis broker config file from the specified source.
     * @param source config file location. Must be a resource loadable from the
     *              current class loader.
     * @return the configuration as read from the specified source.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws Exception if the configuration couldn't be located or an error
     * occurred while reading it.
     */
    public static Configuration readBrokerXmlConfig(URI source)
            throws Exception {  // (*)
        requireNonNull(source, "source");

        FileDeploymentManager reader =
                new FileDeploymentManager(source.toString());
        FileConfiguration config = new FileConfiguration();
        reader.addDeployable(config);
        reader.readConfiguration();

        return config;
    }
    // (*) code lifted from EmbeddedActiveMQ.initStart()

    /**
     * @return an configuration builder with an empty configuration.
     */
    public static Builder<Void, Configuration> empty() {
        return Builder.make(ConfigurationImpl::new);
    }

    /**
     * @return a configuration builder with a configuration populated from
     * the default Artemis broker file.
     */
    public static Builder<Void, Configuration> fromXml() {
        return fromXml(defaultBrokerXmlConfigLocation());
    }

    /**
     * A configuration builder with a configuration populated from the
     * specified source.
     * @param source config file location. Must be a resource loadable from the
     *              current class loader.
     * @return the builder.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static Builder<Void, Configuration> fromXml(URI source) {
        requireNonNull(source, "source");
        return Builder.make(unchecked(() -> readBrokerXmlConfig(source)));
    }

}

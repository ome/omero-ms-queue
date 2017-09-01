package kew.providers.artemis.config.transport;

/**
 * Configuration of a connection to an Artemis sever.
 * The connection can either be a regular network connection or an embedded
 * one, i.e. one you use to connect to a broker running in the same JVM.
 */
public interface ConnectorConfig extends EndpointConfig {

}

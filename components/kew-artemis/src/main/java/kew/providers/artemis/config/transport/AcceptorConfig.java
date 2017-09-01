package kew.providers.artemis.config.transport;

/**
 * Configuration of an Artemis sever endpoint accepting client connections.
 * This can either be a regular network acceptor or an embedded one, i.e.
 * one that accepts connections from clients running in the same JVM.
 */
public interface AcceptorConfig extends EndpointConfig {

}

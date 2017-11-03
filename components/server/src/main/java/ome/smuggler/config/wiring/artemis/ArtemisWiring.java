package ome.smuggler.config.wiring.artemis;

import javax.jms.ConnectionFactory;

import org.apache.activemq.artemis.api.core.client.ServerLocator;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kew.providers.artemis.ServerConnector;
import kew.providers.artemis.runtime.ClientSessions;

/**
 * Singleton beans for Artemis client resources that have to be shared and
 * reused. 
 */
@Configuration
public class ArtemisWiring {
    
    @Bean
    public ServerConnector artemisServerConnector(ConnectionFactory cf)
            throws Exception {
        ActiveMQConnectionFactory factory = (ActiveMQConnectionFactory) cf;
        ServerLocator locator = factory.getServerLocator();
        return new ServerConnector(locator, ClientSessions.defaultSession());
    }

}

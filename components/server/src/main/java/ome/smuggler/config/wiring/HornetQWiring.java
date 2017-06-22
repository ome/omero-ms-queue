package ome.smuggler.config.wiring;

import javax.jms.ConnectionFactory;

import org.hornetq.api.core.client.ServerLocator;
import org.hornetq.jms.client.HornetQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ome.smuggler.providers.q.ServerConnector;


/**
 * Singleton beans for HornetQ client resources that have to be shared and
 * reused. 
 */
@Configuration
public class HornetQWiring {
    
    @Bean
    public ServerConnector hornetQServerConnector(ConnectionFactory cf) 
            throws Exception {
        HornetQConnectionFactory factory = (HornetQConnectionFactory) cf; 
        ServerLocator locator = factory.getServerLocator();
        return new ServerConnector(locator);
    }

}

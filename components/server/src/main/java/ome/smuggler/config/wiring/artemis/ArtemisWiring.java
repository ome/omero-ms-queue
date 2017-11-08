package ome.smuggler.config.wiring.artemis;

import static kew.providers.artemis.runtime.ClientSessions.defaultSession;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kew.providers.artemis.ServerConnector;
import kew.providers.artemis.runtime.DeploymentSpec;
import kew.providers.artemis.runtime.EmbeddedServer;

/**
 * Singleton beans for Artemis client resources that have to be shared and
 * reused.
 */
@Configuration
public class ArtemisWiring {

    @Bean(destroyMethod = "stop")
    public EmbeddedServer artemisEmbeddedServer(DeploymentSpec spec)
            throws Exception {
        return EmbeddedServer.start(spec);
    }

    @Bean
    public ServerConnector artemisServerConnector(EmbeddedServer server)
            throws Exception {
        return server.startClientSession(defaultSession());
    }

}

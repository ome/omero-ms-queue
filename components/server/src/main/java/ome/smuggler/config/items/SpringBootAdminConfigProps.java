package ome.smuggler.config.items;

import static util.config.props.JPropAccessorFactory.makeURI;
import static util.config.props.JPropKey.key;

import java.net.URI;

import util.config.props.JPropAccessor;

/**
 * Accessors for <a href="https://github.com/codecentric/spring-boot-admin">
 * Spring Boot Admin</a> properties.
 */
public class SpringBootAdminConfigProps {

    public static final String SpringBootAdminPrefix = "spring.boot.admin";
   
    public static JPropAccessor<URI> adminServerUrl() {
        return makeURI(key(SpringBootAdminPrefix, "url"));
    }
    
}

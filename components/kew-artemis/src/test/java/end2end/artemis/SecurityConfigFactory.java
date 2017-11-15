package end2end.artemis;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static kew.providers.artemis.config.AddressMatchers.anyAddress;
import static kew.providers.artemis.config.security.AddressPermission.*;
import static kew.providers.artemis.config.security.RolePermissionsBuilder.role;
import static kew.providers.artemis.config.security.SecurityProps.*;
import static kew.providers.artemis.config.security.SecurityManagerProps.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.activemq.artemis.core.config.Configuration;

import kew.providers.artemis.config.security.ActiveMQJAASSecurityManagerAdapter;
import kew.providers.artemis.config.CoreConfigFactory;
import util.object.Builder;

/**
 * This class goes hand in hand with the security config in the resources
 * directory. Keep them in sync!
 */
public class SecurityConfigFactory {

    private static final String JaasConfigFile = "login.config";
    private static final String JaasConfigKey = "java.security.auth.login.config";
    private static final String RolesPropFile = "artemis-roles.properties";
    private static final String UsersPropFile = "artemis-users.properties";
    private static final String Domain = "activemq";
    private static final String ReadOnlyRole = "testers";
    private static final String ReadOnlyUser = "tasty";
    private static final String ReadWriteRole = "users";
    private static final String ReadWriteUser = "sam";


    private static Properties readPropsOnClasspath(String file)
            throws IOException {
        try (InputStream in = SecurityConfigFactory.class
                                            .getClassLoader()
                                            .getResourceAsStream(file)) {
            Properties props = new Properties();
            props.load(in);
            return props;
        }
    }

    public static void setJaasConfig() throws Exception {
        URL file = SecurityConfigFactory.class
                                        .getClassLoader()
                                        .getResource(JaasConfigFile);  // (*)
        String path = Paths.get(file.toURI()).toString();
        System.setProperty(JaasConfigKey, path);
    }
    // (*) should be something like
    // file:/abs/path/to/omero-ms-queue/components/kew-artemis/build/resources/test/login.config

    public static void clearJaasConfig() {
        System.clearProperty(JaasConfigKey);
    }

    private final Properties roles;
    private final Properties users;

    public SecurityConfigFactory() throws IOException {
        this.roles = readPropsOnClasspath(RolesPropFile);
        this.users = readPropsOnClasspath(UsersPropFile);
        checkProps();
    }

    private void checkProps() throws IOException {
        assertThat(roles.stringPropertyNames(),
                   hasItems(ReadOnlyRole, ReadWriteRole));

        assertThat(users.stringPropertyNames(),
                   hasItems(ReadOnlyUser, ReadWriteUser));

        assertThat(roles.getProperty(ReadWriteRole),
                   not(containsString(ReadOnlyUser)));
    }

    public Builder<Void, ActiveMQJAASSecurityManagerAdapter> manager() {
        return defaultSecurityManager().with(domain(Domain));
    }

    public Builder<Void, Configuration> config() {
        return CoreConfigFactory
                .empty()
                .with(securityEnabled(true))
                .with(addressPermissionsFor(anyAddress(), role(ReadWriteRole)
                                                          .can(Send, Consume)))
                .with(addressPermissionsFor(anyAddress(), role(ReadOnlyRole)
                                                          .can(Consume)));
    }

    public String readOnlyUsername() {
        return ReadOnlyUser;
    }

    public String readOnlyUsernamePassword() {
        return users.getProperty(ReadOnlyUser);
    }

    public String readWriteUsername() {
        return ReadWriteUser;
    }

    public String readWriteUsernamePassword() {
        return users.getProperty(ReadWriteUser);
    }

}

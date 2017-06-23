package ome.smuggler.core.types;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import ome.smuggler.config.items.OmeCliConfig;
import org.junit.Test;

import java.time.Duration;

public class OmeCliConfigReaderTest {

    private static OmeCliConfig config() {
        OmeCliConfig cfg = new OmeCliConfig();
        cfg.setOmeCliJarPath("no-where");

        return cfg;
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullConfig() {
        new OmeCliConfigReader(null);
    }

    @Test
    public void defaultKeepAliveToValueInOmeroDefault() {
        OmeCliConfigReader target = new OmeCliConfigReader(config());
        Duration actual = target.sessionKeepAliveInterval();

        assertThat(actual, is(OmeroDefault.SessionKeepAliveInterval));
    }

    @Test
    public void keepAliveIsInMinutes() {
        OmeCliConfig cfg = config();
        cfg.setSessionKeepAliveInterval((long)2);

        OmeCliConfigReader target = new OmeCliConfigReader(cfg);
        Duration actual = target.sessionKeepAliveInterval();

        assertThat(actual, is(Duration.ofMinutes(2)));
    }

}

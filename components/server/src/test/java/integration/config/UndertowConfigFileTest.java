package integration.config;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import util.config.ConfigProvider;
import ome.smuggler.config.data.UndertowYmlFile;
import ome.smuggler.config.items.UndertowConfig;
import ome.smuggler.config.providers.PriorityConfigProvider;
import ome.smuggler.config.providers.UndertowConfigProvider;
import ome.smuggler.run.RunnableApp;
import ome.smuggler.run.UndertowYmlGen;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=UndertowConfigProvider.class)
public class UndertowConfigFileTest extends ConfigFileTest<UndertowConfig> {

    @Autowired
    private UndertowConfigProvider configProvider;
    
    @Override
    protected PriorityConfigProvider<UndertowConfig> getConfigProvider() {
        return configProvider;
    }

    @Override
    protected RunnableApp getFileGenerator() {
        return new UndertowYmlGen();
    }

    @Override
    protected ConfigProvider<UndertowConfig> getFileContents() {
        return new UndertowYmlFile();
    }

}


package integration.config;

import ome.smuggler.config.data.CryptoYmlFile;
import ome.smuggler.config.items.CryptoConfig;
import ome.smuggler.config.providers.CryptoConfigProvider;
import ome.smuggler.config.providers.PriorityConfigProvider;
import ome.smuggler.run.CryptoYmlGen;
import ome.smuggler.run.RunnableApp;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import util.config.ConfigProvider;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=CryptoConfigProvider.class)
public class CryptoConfigFileTest extends ConfigFileTest<CryptoConfig> {

    @Autowired
    private CryptoConfigProvider configProvider;

    @Override
    protected PriorityConfigProvider<CryptoConfig> getConfigProvider() {
        return configProvider;
    }

    @Override
    protected RunnableApp getFileGenerator() {
        return new CryptoYmlGen();
    }

    @Override
    protected ConfigProvider<CryptoConfig> getFileContents() {
        return new CryptoYmlFile();
    }

}

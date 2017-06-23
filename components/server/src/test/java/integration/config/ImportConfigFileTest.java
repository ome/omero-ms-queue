package integration.config;

import ome.smuggler.config.data.ImportYmlFile;
import ome.smuggler.config.items.ImportConfig;
import ome.smuggler.config.providers.ImportConfigProvider;
import ome.smuggler.config.providers.PriorityConfigProvider;
import ome.smuggler.run.ImportYmlGen;
import ome.smuggler.run.RunnableApp;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import util.config.ConfigProvider;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=ImportConfigProvider.class)
public class ImportConfigFileTest extends ConfigFileTest<ImportConfig> {

    @Autowired
    private ImportConfigProvider configProvider;
    
    @Override
    protected PriorityConfigProvider<ImportConfig> getConfigProvider() {
        return configProvider;
    }

    @Override
    protected RunnableApp getFileGenerator() {
        return new ImportYmlGen();
    }

    @Override
    protected ConfigProvider<ImportConfig> getFileContents() {
        return new ImportYmlFile();
    }

}

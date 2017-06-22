package integration.config;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ome.smuggler.config.data.MountPointsYmlFile;
import ome.smuggler.config.items.MountPointsConfig;
import ome.smuggler.config.providers.MountPointsConfigProvider;
import ome.smuggler.config.providers.PriorityConfigProvider;
import ome.smuggler.run.MountPointsYmlGen;
import ome.smuggler.run.RunnableApp;
import util.config.ConfigProvider;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= MountPointsConfigProvider.class)
public class MountPointsConfigFileTest
        extends ConfigFileTest<MountPointsConfig> {

    @Autowired
    private MountPointsConfigProvider configProvider;

    @Override
    protected PriorityConfigProvider<MountPointsConfig> getConfigProvider() {
        return configProvider;
    }

    @Override
    protected RunnableApp getFileGenerator() {
        return new MountPointsYmlGen();
    }

    @Override
    protected ConfigProvider<MountPointsConfig> getFileContents() {
        return new MountPointsYmlFile();
    }

}


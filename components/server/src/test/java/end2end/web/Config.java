package end2end.web;

import java.io.IOException;
import java.nio.file.Path;

import ome.smuggler.config.BaseDataDir;
import ome.smuggler.config.data.DevImportConfigSource;
import ome.smuggler.config.data.DevMailConfigSource;
import ome.smuggler.config.wiring.mail.MailServiceBeans;
import ome.smuggler.core.service.file.TaskFileStore;
import ome.smuggler.core.types.ImportConfigSource;
import ome.smuggler.core.types.MailConfigSource;
import ome.smuggler.core.types.MailId;

public class Config {

    public final Path baseDataDir;
    public final ImportConfigSource importConfig;
    public final MailConfigSource mailConfig;
    public final TaskFileStore<MailId> failedMailStore;

    public Config() throws IOException {
        baseDataDir = BaseDataDir
                    .storeTempDir(BaseDataDir.SysPropKey, "smuggler-tests");

        importConfig = new DevImportConfigSource();
        mailConfig = new DevMailConfigSource();
        failedMailStore = new MailServiceBeans().failedMailStore(mailConfig);
        // ...exact copies of the ones instantiated in the Spring container.
    }

}

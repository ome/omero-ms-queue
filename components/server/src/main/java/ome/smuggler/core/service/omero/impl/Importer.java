package ome.smuggler.core.service.omero.impl;

import static java.util.Objects.requireNonNull;

import ome.smuggler.core.service.omero.ImportService;
import ome.smuggler.core.types.ImportConfigSource;
import ome.smuggler.core.types.ImportInput;

import java.nio.file.Path;

/**
 * OMERO import service implementation.
 */
public class Importer implements ImportService {

    private final OmeroEnv env;
    private final ImportConfigSource cfg;

    /**
     * Creates a new instance.
     * @param env the service environment.
     * @param cfg the import configuration.
     */
    public Importer(OmeroEnv env, ImportConfigSource cfg) {
        requireNonNull(env, "env");
        requireNonNull(cfg, "cfg");

        this.env = env;
        this.cfg = cfg;
    }

    @Override
    public boolean run(ImportInput data, Path importLog) {
        Path importTarget = env.fileResolver()
                               .forceLocalPath(data.getTarget());  // (*)
        ImporterCommandBuilder cliOmeroImporter =
                new ImporterCommandBuilder(env.config(),
                        data, importTarget, cfg.niceCommand());
        OmeCliCommandRunner runner =
                new OmeCliCommandRunner(env, cliOmeroImporter);
        return runner.run(importLog);
    }
    /* (*) URI resolution. We're assuming the file is local or comes from a
     * network share visible to both client and smuggler. Going forward we
     * might replace this with a more sophisticated URI to file resolution
     * that also caters for FTP, HTTP, and IPFS (?!) but the OMERO import
     * library will have to be modified to read files from FTP or HTTP...
     */
}

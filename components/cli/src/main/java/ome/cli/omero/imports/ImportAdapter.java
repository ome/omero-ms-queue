package ome.cli.omero.imports;

import ome.cli.cmd.ExitCode;
import ome.formats.importer.cli.CommandLineImporter;

/**
 * Calls the {@link CommandLineImporter}, exiting with an internal error if an
 * exception is thrown.
 */
public class ImportAdapter {

    public static void main(String[] args) {
        try {
            CommandLineImporter.main(args);  // calls System.exit
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(ExitCode.InternalError.code());
        }
    }

}

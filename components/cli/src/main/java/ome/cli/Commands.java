package ome.cli;

import ome.cli.omero.imports.ImportAdapter;
import ome.cli.omero.session.Close;
import ome.cli.omero.session.Create;
import ome.cli.omero.session.CreateFromExisting;
import ome.cli.omero.session.KeepAlive;

import java.util.function.Consumer;

/**
 * Associates each available command to its {@code main} method.
 */
public enum Commands {

    /**
     * The {@link Create} session command entry point.
     */
    CreateSession(Create::main),

    /**
     * The {@link CreateFromExisting} session command entry point.
     */
    CreateSessionFromExisting(CreateFromExisting::main),

    /**
     * The session {@link KeepAlive} command entry point.
     */
    SessionKeepAlive(KeepAlive::main),

    /**
     * The {@link Close} session command entry point.
     */
    CloseSession(Close::main),

    /**
     * The {@link ImportAdapter CLI importer} entry point.
     */
    Import(ImportAdapter::main);

    private final Consumer<String[]> commandMain;

    Commands(Consumer<String[]> commandMain) {
        this.commandMain = commandMain;
    }

    /**
     * @return the command's {@code main} method.
     */
    public Consumer<String[]> mainMethod() {
        return commandMain;
    }

}

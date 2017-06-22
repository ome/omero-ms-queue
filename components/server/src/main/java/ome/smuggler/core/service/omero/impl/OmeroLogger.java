package ome.smuggler.core.service.omero.impl;

import ome.smuggler.core.service.log.BaseLogger;
import ome.smuggler.core.service.log.LogService;
import util.object.Pair;
import util.string.Strings;

import java.nio.file.Path;
import java.util.stream.Stream;

import static util.object.Pair.pair;


/**
 * Logging methods for the OMERO services.
 */
public class OmeroLogger extends BaseLogger {

    private static Stream<Pair<Object, Object>> summary(OmeCliCommandBuilder cmd,
                                                        int exitCode) {
        return Stream.of(
                pair("Command", cmd),
                pair("Exit Code", exitCode));
    }

    private static Stream<Pair<Object, Object>> summary(OmeCliCommandBuilder cmd,
                                                        int exitCode,
                                                        String cmdOutput) {
        return Stream.of(
                pair("Command", cmd),
                pair("Exit Code", exitCode),
                pair("Command Output", cmdOutput));
    }

    private static Stream<Pair<Object, Object>> summary(OmeCliCommandBuilder cmd,
                                                        Exception e) {
        return Stream.of(
                pair("Command", cmd),
                pair("Cause of Failure", e.getMessage()),
                pair("Error Detail", Strings.write(e::printStackTrace)));
    }


    public OmeroLogger(LogService service) {
        super(service);
    }

    public void success(OmeCliCommandBuilder cmd, int exitCode) {
        info(cmd, fieldsWriter(
                    String.format("OMERO %s command succeeded.", cmd.commandName()),
                    summary(cmd, exitCode)));
    }

    public void failure(OmeCliCommandBuilder cmd, int exitCode, String cmdOutput) {
        error(cmd, fieldsWriter(
                String.format("OMERO %s command failed.", cmd.commandName()),
                summary(cmd, exitCode, cmdOutput)));
    }

    public void failure(OmeCliCommandBuilder cmd, int exitCode, Path outputFile) {
        error(cmd, fieldsWriter(
                String.format("OMERO %s command failed.", cmd.commandName()),
                summary(cmd, exitCode,
                        String.format("redirected to file -> %s", outputFile))));
    }

    public void failure(OmeCliCommandBuilder cmd, Exception e) {
        error(cmd, fieldsWriter(
                String.format("Failed to run OMERO %s command.", cmd.commandName()),
                summary(cmd, e)));
    }

}

package ome.smuggler.core.service.omero.impl;

import static java.util.Objects.requireNonNull;

import java.net.URI;
import java.nio.file.Path;

import ome.smuggler.core.types.ImportInput;
import ome.smuggler.core.types.OmeCliConfigSource;
import util.runtime.BaseProgramArgument;
import util.runtime.CommandBuilder;
import util.runtime.ListProgramArgument;
import util.runtime.ProgramArgument;
import util.runtime.jvm.JvmCmdBuilder;

/**
 * Build the command line to call the OMERO importer.
 */
public class ImporterCommandBuilder extends OmeCliCommandBuilder {

    private static final String SessionKeyOpt = "-k";

    private final ImportInput importArgs;
    private final Path importPath;
    private final CommandBuilder niceCommand;
    
    /**
     * Creates a new instance to build a command line from the given data.
     * @param config configuration for the OMERO CLI commands.
     * @param importArgs details what to import.
     * @param importPath file or directory to import.
     * @param niceCommand "nice" command to set process priority.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public ImporterCommandBuilder(OmeCliConfigSource config,
                                  ImportInput importArgs,
                                  Path importPath,
                                  CommandBuilder niceCommand) {
        super(config);
        requireNonNull(importArgs, "importArgs");
        requireNonNull(importPath, "importPath");
        requireNonNull(niceCommand, "niceCommand");
        
        this.importArgs = importArgs;
        this.importPath = importPath;
        this.niceCommand = niceCommand;
    }
    
    private ListProgramArgument<String> server() {
        URI omero = importArgs.getOmeroHost();
        return arg("-s", omero.getHost(), 
                   "-p", String.valueOf(omero.getPort()),
                   SessionKeyOpt, importArgs.getSessionKey());
    }
    
    private ListProgramArgument<String> name() {
        return optionalArg("-n", importArgs.getName());
                         
    }
    
    private ListProgramArgument<String> description() {
        return optionalArg("-x", importArgs.getDescription());                     
    }
    
    private ListProgramArgument<String> datasetId() {
        return optionalArg("-d", importArgs.getDatasetId());                     
    }
    
    private ListProgramArgument<String> screenId() {
        return optionalArg("-r", importArgs.getScreenId());
    }
    
    private CommandBuilder[] textAnnotations() {
        return importArgs.getTextAnnotations()
                         .map(a -> arg("--annotation-ns", a.namespace(), 
                                       "--annotation-text", a.text()))
                         .toArray(CommandBuilder[]::new);
    }
    
    private CommandBuilder[] annotationIds() {
        return importArgs.getAnnotationIds()
                         .map(id -> arg("--annotation-link", id.toString()))
                         .toArray(CommandBuilder[]::new);
    }
    
    private ProgramArgument<String> importTarget() {
        return new BaseProgramArgument<>(importPath.toString());
    }

    private JvmCmdBuilder buildJavaCommandLine(JvmCmdBuilder java) {
        return java
                .addApplicationArgument(server())
                .addApplicationArgument(name())
                .addApplicationArgument(description())
                .addApplicationArgument(datasetId())
                .addApplicationArgument(screenId())
                .addApplicationArgument(textAnnotations())
                .addApplicationArgument(annotationIds())
                .addApplicationArgument(importTarget());
    }

    @Override
    protected String commandName() {
        return "Import";
    }

    @Override
    protected CommandBuilder assembleArguments(JvmCmdBuilder java) {
        return niceCommand.join(buildJavaCommandLine(java));
    }

    @Override
    public String toString() {
        return new OmeCliCommandPrinter(this).printMasking(SessionKeyOpt);
    }

}

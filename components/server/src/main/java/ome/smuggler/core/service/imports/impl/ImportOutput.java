package ome.smuggler.core.service.imports.impl;

import static java.nio.file.StandardOpenOption.*;
import static java.util.Objects.requireNonNull;
import static ome.smuggler.core.service.imports.impl.ImportOutputFormatter.*;
import static util.error.Exceptions.throwAsIfUnchecked;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import ome.smuggler.core.types.ImportLogPath;
import ome.smuggler.core.types.QueuedImport;


public class ImportOutput {
    
    private final QueuedImport task;
    private final Path outputFile;
    
    public ImportOutput(ImportLogPath outputFile, QueuedImport task) {
        requireNonNull(outputFile, "outputFile");
        requireNonNull(task, "task");
        
        this.task = task;
        this.outputFile = outputFile.get();
    }

    private void output(String line) throws IOException {
        List<String> content = Collections.singletonList(line);
        Files.write(outputFile, content, CREATE, WRITE, APPEND);
    }
        
    public void writeQueued() throws IOException {
        output(queued(task));
    }
    
    public void writeHeader()
            throws IOException {
        output(header(task));
    }
    
    public void writeFooter(boolean success) throws IOException {
        output(footer(success));
    }
    
    public void writeFooter(Exception e) {
        try {
            output(footer(e));
        } catch (IOException ioe) {
            throwAsIfUnchecked(ioe);
        }
    }
    
}

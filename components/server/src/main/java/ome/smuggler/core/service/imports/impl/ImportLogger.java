package ome.smuggler.core.service.imports.impl;

import static util.object.Pair.pair;

import java.io.PrintWriter;
import java.util.function.Consumer;
import java.util.stream.Stream;

import ome.smuggler.core.service.log.BaseLogger;
import ome.smuggler.core.service.log.LogService;
import ome.smuggler.core.types.ImportInput;
import ome.smuggler.core.types.ProcessedImport;
import ome.smuggler.core.types.QueuedImport;
import util.object.Pair;

/**
 * Logging methods for the import service.
 */
public class ImportLogger extends BaseLogger {

    private static Stream<Pair<Object, Object>> summary(QueuedImport data) {
        ImportInput req = data.getRequest();
        return Stream.of(
                pair("Import ID", data.getTaskId().id()),
                pair("Experimenter Email", req.getExperimenterEmail().get()),
                pair("Import Target", req.getTarget()),
                pair("Server", req.getOmeroHost()));
    }

    private static Stream<Pair<Object, Object>> summary(
            ProcessedImport data, boolean includeImportId) {
        Stream<Pair<Object, Object>> xs = Stream.of(
                pair("Batch ID", data.batchId().id()),
                pair("Finalisation Phase", data.status()));

        Stream<Pair<Object, Object>> ys = Stream.empty();
        if (includeImportId) {
            ys = Stream.of(
                    pair("Import ID", data.queued().getTaskId().id()));
        }

        return Stream.concat(xs, ys);
    }
    
    private static Consumer<PrintWriter> writer(String header, 
            QueuedImport data) {
        return fieldsWriter(header, summary(data));
    }


    public ImportLogger(LogService service) {
        super(service);
    }

    public void importQueued(QueuedImport data) {
        info(data, writer("OMERO import task queued.", data));
    }
    
    public void importStart(QueuedImport data) {
        info(data, writer("OMERO import task starting.", data));
    }
    
    public void importSuccessful(QueuedImport data) {
        info(data, writer("OMERO import task completed successfully.", data));
    }
    
    /**
     * Logs an error when an import fails permanently, after having been 
     * retried.
     * @param data details of the original import request.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public void importPermanentFailure(QueuedImport data) {
        error(data, writer("OMERO import task failed permanently.", data));
    }

    public void importBatchCompleted(ProcessedImport data) {
        info(data, fieldsWriter("OMERO import batch completed.",
                Stream.of(
                    pair("Batch ID", data.batchId().id()),
                    pair("Last Import ID", data.queued().getTaskId().id())
                ))
        );
    }

    public void importBatchUpdateFinalisationFailed(ProcessedImport data) {
        error(data, fieldsWriter(
                "OMERO import batch failed to mark an import as completed. " +
                "The batch cannot be finalised, but its imports will still " +
                "be run.",
                summary(data, true))
        );
    }

    public void importBatchCompletionFinalisationFailed(ProcessedImport data) {
        error(data, fieldsWriter(
                "OMERO import batch finalisation failed. " +
                "The batch's imports have been run, but users won't be " +
                "notified and OMERO sessions won't be closed.",
                summary(data, false))
        );
    }

    public void importBatchDisposalFinalisationFailed(ProcessedImport data) {
        error(data, fieldsWriter(
                "OMERO import batch finalisation failed. " +
                "The batch's imports have been run, users have been " +
                "notified, and OMERO sessions have been closed. But batch's " +
                "files won't be deleted.",
                summary(data, false))
        );
    }

}

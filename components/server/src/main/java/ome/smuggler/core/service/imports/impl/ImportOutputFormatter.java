package ome.smuggler.core.service.imports.impl;

import static util.string.Strings.write;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import ome.smuggler.core.types.ImportInput;
import ome.smuggler.core.types.QueuedImport;

public class ImportOutputFormatter {

    private static void field(PrintWriter out, String name, Object value) {
        out.format("%s: %s%n", name, value);
    }
    
    private static void timestamp(PrintWriter out) {
        Date now = Calendar.getInstance().getTime();
        String value = DateFormat.getDateTimeInstance().format(now);
        field(out, "Timestamp", value);
    }
    
    private static void importId(PrintWriter out, QueuedImport task) {
        field(out, "Import ID", task.getTaskId());
    }
    
    private static void importTarget(PrintWriter out, ImportInput req) {
        field(out, "Import Target", req.getTarget());
    }
    
    private static void omeroServer(PrintWriter out, ImportInput req) {
        field(out, "OMERO Server", req.getOmeroHost());
    }
    
    private static void experimenterEmail(PrintWriter out, ImportInput req) {
        field(out, "Experimenter's Email", req.getExperimenterEmail());
    }
    
    private static void summary(PrintWriter out, QueuedImport task) {
        ImportInput req = task.getRequest();
        
        timestamp(out);
        importId(out, task);
        importTarget(out, req);
        omeroServer(out, req);
        experimenterEmail(out, req);
    }
    
    private static void succeeded(PrintWriter out, boolean success) {
        timestamp(out);
        field(out, "Succeeded", success);
    }
    
    public static String queued(QueuedImport task) {
        return write(out -> {    
            out.println("====================> Queued OMERO Import:");
            summary(out, task);
            out.println("============================================");
        });
    }
    
    public static String header(QueuedImport task) {
        return write(out -> {
            out.println("====================> Starting OMERO Import:");
            summary(out, task);
            out.println("============================================");
        });
    }
    
    public static String footer(boolean success) {
        return write(out -> {
            out.println();
            out.println("=================> OMERO Import Run Report:");
            succeeded(out, success);
            out.println("============================================");
        });
    }
    
    public static String footer(Exception e) {
        return write(out -> {
            out.println();
            out.println("=================> OMERO Import Run Report:");
            succeeded(out, false);
            field(out, "Cause of Failure", e.getMessage());
            out.println("Error Detail:");
            e.printStackTrace(out);
            out.println("============================================");
        });
    }
    
}

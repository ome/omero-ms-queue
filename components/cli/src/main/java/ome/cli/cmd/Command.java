package ome.cli.cmd;

import java.io.PrintStream;

/**
 * Encapsulates an interaction with OMERO.
 * The intended usage is that a JVM is started to execute it.
 */
public interface Command {
    
    /**
     * Carries out some OMERO tasks.  
     * @param out the JVM standard output.
     * @return the JVM exit code.
     * @throws Exception if an error occurs.
     */
    ExitCode exec(PrintStream out) throws Exception;
    
}

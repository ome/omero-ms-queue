package ome.smuggler.config.items;

import java.util.Arrays;
import java.util.Objects;

/**
 * Specifies how to configure import logs.
 * The available settings:
 * <ul>
 *  <li>{@link #setImportLogDir(String) Log directory path}.
 *  Path to the directory where all import logs will be kept. Directories in the
 *  path will be created if needed. 
 *  </li>
 *  <li>{@link #setLogRetentionMinutes(Long) Log file retention period}.
 *  How long to keep an import run's log around after the import has completed.
 *  Past the specified amount of minutes, the file will be deleted.
 *  </li>
 *  <li>{@link #setRetryIntervals(Long[]) Retry intervals}.
 *  How many times and at which intervals to retry a failed import.
 *  If an import fails, it will be retried after the number of minutes given in
 *  the first slot of this array; if the retry fails, it will be attempted again
 *  after the number of minutes given in the second slot of this array; and so
 *  on until the import succeeds or the last slot is used. So a failed import
 *  will be retried at most a number of times equal to the length of this array;
 *  if the array is {@code null} or empty, no retries will ever be attempted.
 *  </li>
 *  <li>{@link #setFailedImportLogDir(String) Failed log directory path}.
 *  Path to the directory where to copy the log files of failed imports. 
 *  Directories in the path will be created if needed. 
 *  Note that the original file will still be available in the {@link 
 *  #getImportLogDir() log directory} but will be removed past the {@link 
 *  #getLogRetentionMinutes() retention period}. However, its copy in the 
 *  failed log directory will be kept indefinitely, the system administrator 
 *  will have to explicitly delete it after resolving the issue that caused 
 *  the failure.
 *  </li>
 *  <li>{@link #setNiceCommand(String) Nice command}.
 *  Optional command to use to set the priority of processes that run OMERO
 *  imports. If specified, this string will be prepended to the command used
 *  to spawn any process that runs an import. On Unix-like platforms you could
 *  use the {@code nice} utility; e.g. {@code nice -n 10}. As for Windows, you
 *  could use <a href="https://github.com/c0c0n3/winice">winice</a> with the
 *  the exact same command you would use for Unix. Or you could use a similar
 *  program, e.g. {@code nice} from the GNU Core Utils for Windows or even a
 *  GUI like <a href="http://www.nirsoft.net/utils/advanced_run.html">
 *  AdvancedRun</a>. Whatever you do, do <em>not</em> use the Windows shell
 *  (as in e.g. {@code cmd /c start /belownormal /wait /b}) because it just
 *  doesn't work nicely when called from Java.
 *  </li>
 *  <li>{@link #setBatchStatusDbDir(String) Batch status DB directory path}.
 *  Path to the directory where to keep the files of the batch status DB.
 *  This DB stores data to keep track of progress of a running import batch.
 *  Directories in the path will be created if needed.
 *  </li>
 *  <li>{@link #setBatchStatusDbLockStripes(Long) Number of lock stripes for
 *  the batch status DB}.
 *  Optional number of lock stripes the DB should use to control parallel access
 *  to the data. If specified, it has to be a positive integer.
 *  </li>
 * </ul>
 */
public class ImportConfig {
    /* NB this has to be a Java Bean (i.e. getters/setters, no args ctor) to
     * be (de-)serialized painlessly by SnakeYaml.  
     */ 

    private String importLogDir;
    private Long logRetentionMinutes;
    private Long[] retryIntervals;
    private String failedImportLogDir;
    private String niceCommand;
    private String batchStatusDbDir;
    private Long batchStatusDbLockStripes;

    public String getImportLogDir() {
        return importLogDir;
    }
    
    public void setImportLogDir(String importLogDir) {
        this.importLogDir = importLogDir;
    }

    public Long getLogRetentionMinutes() {
        return logRetentionMinutes;
    }

    public void setLogRetentionMinutes(Long retentionMinutes) {
        this.logRetentionMinutes = retentionMinutes;
    }
    
    public Long[] getRetryIntervals() {
        return retryIntervals;
    }

    public void setRetryIntervals(Long[] retryIntervals) {
        this.retryIntervals = retryIntervals;
    }
    
    public String getFailedImportLogDir() {
        return failedImportLogDir;
    }

    public void setFailedImportLogDir(String failedImportLogDir) {
        this.failedImportLogDir = failedImportLogDir;
    }

    public String getNiceCommand() {
        return niceCommand;
    }

    public void setNiceCommand(String niceCommand) {
        this.niceCommand = niceCommand;
    }

    public String getBatchStatusDbDir() {
        return batchStatusDbDir;
    }

    public void setBatchStatusDbDir(String batchStatusDbDir) {
        this.batchStatusDbDir = batchStatusDbDir;
    }

    public Long getBatchStatusDbLockStripes() {
        return batchStatusDbLockStripes;
    }

    public void setBatchStatusDbLockStripes(Long batchStatusDbLockStripes) {
        this.batchStatusDbLockStripes = batchStatusDbLockStripes;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
    
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof ImportConfig) {
            return Objects.equals(other.toString(), this.toString());
        }
        return false;
    }
    
    @Override
    public String toString() {
        String xs = Arrays.toString(retryIntervals);
        return String.format("%s | %s | %s | %s | %s | %s | %s",
                importLogDir, failedImportLogDir, logRetentionMinutes, xs,
                batchStatusDbDir, batchStatusDbLockStripes,
                niceCommand);
    }
    
}

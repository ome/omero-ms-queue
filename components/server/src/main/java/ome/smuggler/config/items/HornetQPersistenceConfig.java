package ome.smuggler.config.items;

import java.util.Objects;

/**
 * Specifies operational parameters for the embedded HornetQ server. 
 */
public class HornetQPersistenceConfig {
    /* NB this has to be a Java Bean (i.e. getters/setters, no args ctor) to
     * be (de-)serialized painlessly by SnakeYaml.  
     */ 

    private boolean persistenceEnabled;
    private String journalDirPath;
    private String largeMessagesDirPath;
    private String bindingsDirPath;
    private String pagingDirPath;
    
    public boolean isPersistenceEnabled() {
        return persistenceEnabled;
    }
    
    public void setPersistenceEnabled(boolean persistenceEnabled) {
        this.persistenceEnabled = persistenceEnabled;
    }
    
    public String getJournalDirPath() {
        return journalDirPath;
    }

    public void setJournalDirPath(String journalDirPath) {
        this.journalDirPath = journalDirPath;
    }

    public String getLargeMessagesDirPath() {
        return largeMessagesDirPath;
    }

    public void setLargeMessagesDirPath(String largeMessagesDirPath) {
        this.largeMessagesDirPath = largeMessagesDirPath;
    }

    public String getBindingsDirPath() {
        return bindingsDirPath;
    }

    public void setBindingsDirPath(String bindingsDirPath) {
        this.bindingsDirPath = bindingsDirPath;
    }

    public String getPagingDirPath() {
        return pagingDirPath;
    }

    public void setPagingDirPath(String pagingDirPath) {
        this.pagingDirPath = pagingDirPath;
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
        if (other instanceof HornetQPersistenceConfig) {
            return Objects.equals(other.toString(), this.toString());
        }
        return false;
    }
    
    @Override
    public String toString() {
        return String.format("%s | %s | %s | %s | %s", 
                persistenceEnabled, journalDirPath, largeMessagesDirPath, 
                bindingsDirPath, pagingDirPath);
    }
    
}

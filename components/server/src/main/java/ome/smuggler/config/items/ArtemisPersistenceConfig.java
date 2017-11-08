package ome.smuggler.config.items;

import java.util.Objects;

/**
 * Specifies operational parameters for the embedded Artemis server.
 */
public class ArtemisPersistenceConfig {
    /* NB this has to be a Java Bean (i.e. getters/setters, no args ctor) to
     * be (de-)serialized painlessly by SnakeYaml.  
     */ 

    private boolean persistenceEnabled;
    private String dataDirPath;
    
    public boolean isPersistenceEnabled() {
        return persistenceEnabled;
    }
    
    public void setPersistenceEnabled(boolean persistenceEnabled) {
        this.persistenceEnabled = persistenceEnabled;
    }

    public String getDataDirPath() {
        return dataDirPath;
    }

    public void setDataDirPath(String dataDirPath) {
        this.dataDirPath = dataDirPath;
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
        if (other instanceof ArtemisPersistenceConfig) {
            return Objects.equals(other.toString(), this.toString());
        }
        return false;
    }
    
    @Override
    public String toString() {
        return String.format("%s | %s",
                             persistenceEnabled, dataDirPath);
    }
    
}

package ome.smuggler.core.service.file.impl;

import static java.util.Objects.requireNonNull;

import java.io.OutputStream;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.stream.Stream;

import ome.smuggler.core.service.file.TaskFileStore;
import ome.smuggler.core.types.TaskIdPath;
import util.io.FileOps;
import util.io.StreamFilter;
import util.lambda.ConsumerE;
import util.object.Identifiable;


/**
 * A {@link TaskFileStore} based on {@link TaskIdPath}.
 */
public class TaskIdPathStore<T extends Identifiable> 
    implements TaskFileStore<T> {

    private final Path storeDir;
    private final Function<String, T> newTaskId;
    
    /**
     * Creates a new instance.
     * @param storeDir the directory where files are stored.
     * @param newTaskId creates a task id from its string representation.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public TaskIdPathStore(Path storeDir, Function<String, T> newTaskId) {
        requireNonNull(storeDir, "storeDir");
        requireNonNull(newTaskId, "newTaskId");
        
        this.storeDir = storeDir;
        this.newTaskId = newTaskId;
    }
    
    @Override
    public Path pathFor(T taskId) {
        requireNonNull(taskId, "taskId");
        return new TaskIdPath(storeDir, taskId).get();
    }

    @Override
    public Stream<T> listTaskIds() {
        return FileOps.listChildFiles(storeDir)
                      .map(Path::getFileName)
                      .map(Path::toString)
                      .map(newTaskId);
    }
    
    @Override
    public void remove(T taskId) {
        FileOps.delete(pathFor(taskId));
    }

    @Override
    public void add(T taskId, ConsumerE<OutputStream> contentWriter) {
        FileOps.writeNew(pathFor(taskId), contentWriter);
    }
    
    @Override 
    public void add(T taskId, Path contentSource) {
        FileOps.copy(contentSource, pathFor(taskId));    
    }

    @Override
    public void replace(T taskId, StreamFilter filter) {
        requireNonNull(taskId, "taskId");
        requireNonNull(filter, "filter");
        if (!listTaskIds().anyMatch(taskId::equals)) {
            throw new IllegalArgumentException("no such taskId: " + taskId.id());
        }

        FileOps.rewrite(pathFor(taskId), filter);
    }

    public Path storeDir() {
        return storeDir;
    }
    
}

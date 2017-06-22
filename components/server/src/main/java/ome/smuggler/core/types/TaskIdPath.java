package ome.smuggler.core.types;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.nio.file.Paths;

import util.object.Identifiable;
import util.object.Wrapper;

/**
 * A path which is the combination of a base path and a unique task identifier.
 */
public class TaskIdPath extends Wrapper<Path> {
    
    private static Path join(Path baseDir, Identifiable taskId) {
        requireNonNull(baseDir, "baseDir");
        requireNonNull(taskId, "taskId");
        
        return Paths.get(baseDir.toString(), taskId.id());
    }

    /**
     * Creates a new path by appending the given ID to the specified base path.
     * @param baseDir the base path.
     * @param taskId the id of task.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public TaskIdPath(Path baseDir, Identifiable taskId) {
        super(join(baseDir, taskId));
    }

}

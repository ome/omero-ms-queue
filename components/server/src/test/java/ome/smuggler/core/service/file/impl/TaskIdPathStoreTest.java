package ome.smuggler.core.service.file.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ome.smuggler.core.service.file.TaskFileStore;
import util.types.UuidString;
import util.lambda.ConsumerE;

public class TaskIdPathStoreTest {

    @Rule
    public final TemporaryFolder storeDir = new TemporaryFolder();

    private TaskFileStore<UuidString> target;
    
    private UuidString addNewTaskIdFileToStore() throws IOException {
        UuidString taskId = new UuidString();
        target.add(taskId, taskId.toString());
        
        return taskId;
    }
    
    @Before
    public void setup() {
        Path p = Paths.get(storeDir.getRoot().getPath());
        target = new TaskIdPathStore<>(p, UuidString::new);
    }
    
    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfFirstArgNull() {
        new TaskIdPathStore<>(null, UuidString::new);
    }
    
    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfSecondArgNull() {
        new TaskIdPathStore<>(Paths.get(""), null);
    }
    
    @Test(expected = NullPointerException.class)
    public void pathForThrowsIfNullArg() {
        target.pathFor(null);
    }
    
    @Test(expected = NullPointerException.class)
    public void removeThrowsIfNullArg() {
        target.remove(null);
    }
    
    @Test(expected = NullPointerException.class)
    public void addThrowsIfFirstArgNull() {
        target.add(null, out -> {});
    }
    
    @Test(expected = NullPointerException.class)
    public void addThrowsIfSecondArgNull() {
        target.add(new UuidString(), (ConsumerE<OutputStream>)null);
    }
    
    @Test
    public void storeInNewDirWillBeInitiallyEmpty() {
        assertThat(target.listTaskIds().count(), is(0L));
    }

    @Test
    public void requestingPathDoesntCreateFile() {
        Path p = target.pathFor(new UuidString());
        assertFalse(Files.exists(p));
        assertThat(target.listTaskIds().count(), is(0L));
    }
    
    @Test
    public void taskIdListedAfterCreatingFile() throws IOException {
        UuidString taskId = addNewTaskIdFileToStore();
        Path taskIdPath = target.pathFor(taskId);
        
        assertTrue(Files.exists(taskIdPath));
        assertThat(target.listTaskIds().count(), is(1L));
        assertThat(target.listTaskIds().findFirst().get(), is(taskId));
    }
    
    @Test
    public void nonTaskIdFilesWouldBeListedToo() throws IOException {
        addNewTaskIdFileToStore();
        storeDir.newFile();
        
        assertThat(target.listTaskIds().count(), is(2L));
    }
    
    @Test
    public void removeDoesNothingIfFileDoesntExist() throws IOException {
        UuidString existingTaskId = addNewTaskIdFileToStore();
        UuidString nonExistentTaskId = new UuidString();
        
        target.remove(nonExistentTaskId);
        
        assertThat(target.listTaskIds().count(), is(1L));
        assertThat(target.listTaskIds().findFirst().get(), is(existingTaskId));
    }
    
    @Test
    public void removeTaskIdFile() throws IOException {
        UuidString taskId1 = addNewTaskIdFileToStore();
        UuidString taskId2 = addNewTaskIdFileToStore();
        
        assertThat(target.listTaskIds().count(), is(2L));
        
        target.remove(taskId1);
        
        assertThat(target.listTaskIds().count(), is(1L));
        assertThat(target.listTaskIds().findFirst().get(), is(taskId2));
    }

    @Test
    public void replaceFileContent() throws IOException {
        UuidString taskId = addNewTaskIdFileToStore();
        String replacement = "new";
        target.replace(taskId, currentValue -> {
            assertThat(currentValue, is(taskId.get()));
            return replacement;
        });
        target.replace(taskId, currentValue -> {
            assertThat(currentValue, is(replacement));
            return "";
        });
    }

}

package ome.smuggler.web.imports;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import ome.smuggler.core.service.file.TaskFileStore;
import ome.smuggler.core.types.ImportId;
import ome.smuggler.web.TaskFileStoreAdapter;


/**
 * Exposes the functionality of the failed import log store to Web clients.
 * A Web client can get the current {@link #listTaskFileUrlPaths() list} of 
 * failed imports (i.e. imports that failed permanently, after having being 
 * retried) and then {@link #streamFileOr404(String, javax.servlet.http.HttpServletResponse)
 * download} the associated import logs. 
 * Normally after the system administrator has resolved the cause of the failure
 * for a specific import, there is no need to keep the log file around anymore
 * and it can be {@link #deleteFile(String) deleted}.
 */
@RestController  // includes @ResponseBody: return vals bound to response body.
@RequestMapping(ImportFailureController.RootPath)
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class ImportFailureController extends TaskFileStoreAdapter<ImportId> {

    public static final String RootPath = "/ome/failed/import";
    
    @Autowired
    private TaskFileStore<ImportId> service;

    @Override
    protected TaskFileStore<ImportId> service() {
        return service;
    }

    @Override
    protected String rootPath() {
        return RootPath;
    }

    @Override
    protected Function<String, ImportId> taskIdFromString() {
        return ImportId::new;
    }
    
}

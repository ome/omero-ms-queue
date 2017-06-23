package ome.smuggler.web.imports;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.io.IOException;
import java.nio.file.Path;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import ome.smuggler.core.service.imports.ImportTracker;
import ome.smuggler.core.types.ImportId;
import ome.smuggler.web.FileStreamer;
import util.servlet.http.Caches;


@RestController  // includes @ResponseBody: return vals bound to response body.
@RequestMapping(ImportController.ImportUrl)
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class ImportStatusController {

    private static final String ImportIdPathVar = "importId";
    
    @Autowired
    private ImportTracker service;
    
    @RequestMapping(method = GET, value = "{" + ImportIdPathVar + "}") 
    public ResponseEntity<String> getStatusUpdate(
            @PathVariable(value=ImportIdPathVar) String importId, 
            HttpServletResponse response) throws IOException {
        ImportId taskId = new ImportId(importId);
        Path importLog = service.importLogPathFor(taskId).get(); 
        FileStreamer streamer = new FileStreamer(importLog, 
                                                 MediaType.TEXT_PLAIN, 
                                                 Caches::doNotCache);
        return streamer.streamOr404(response);
    }
    
}

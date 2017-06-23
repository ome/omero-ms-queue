package ome.smuggler.web.mail;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import ome.smuggler.core.service.file.TaskFileStore;
import ome.smuggler.core.types.MailId;
import ome.smuggler.web.TaskFileStoreAdapter;


@RestController  // includes @ResponseBody: return vals bound to response body.
@RequestMapping(MailFailureController.RootPath)
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class MailFailureController extends TaskFileStoreAdapter<MailId> {
    
    public static final String RootPath = "/ome/failed/mail";
    
    @Autowired
    private TaskFileStore<MailId> service;
    

    @Override
    protected TaskFileStore<MailId> service() {
        return service;
    }

    @Override
    protected String rootPath() {
        return RootPath;
    }

    @Override
    protected Function<String, MailId> taskIdFromString() {
        return MailId::new;
    }
    
}
